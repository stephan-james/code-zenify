package com.github.stephanjames.zenify.actions

import com.github.stephanjames.zenify.services.AppService
import com.github.stephanjames.zenify.visitor.ZenifiedFoldRegion
import com.github.stephanjames.zenify.visitor.ZenifyVisitor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.FoldingModel
import com.intellij.psi.PsiFile
import java.util.*

class ZenifyAction : AnAction() {

    private val originalFoldRegions = WeakHashMap<Editor, List<FoldRegion>>()
    private val zenifiedFoldRegions = WeakHashMap<Editor, List<FoldRegion>>()

    private fun context(event: AnActionEvent): Pair<Editor?, PsiFile?> = Pair(
        event.getData(CommonDataKeys.EDITOR),
        event.getData(CommonDataKeys.PSI_FILE)
    )

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = isEnabled(event)
    }

    private fun isEnabled(event: AnActionEvent): Boolean {
        val (editor, psiFile) = context(event)
        return editor != null && psiFile != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val (editor, psiFile) = context(event)
        if (editor == null || psiFile == null) {
            return
        }
        ActionRunner(originalFoldRegions, zenifiedFoldRegions, editor, psiFile).run()
    }

    class ActionRunner(
        private val originalFoldRegions: MutableMap<Editor, List<FoldRegion>>,
        private val zenifiedFoldRegions: MutableMap<Editor, List<FoldRegion>>,
        private val editor: Editor,
        private val psiFile: PsiFile
    ) : Runnable {

        override fun run() {
            if (isFolded()) {
                unfold()
            } else {
                fold()
            }
        }

        private fun isFolded() =
            zenifiedFoldRegions.containsKey(editor)

        private val foldingModel: FoldingModel
            get() = editor.foldingModel

        private val allFoldRegions: List<FoldRegion>
            get() = foldingModel.allFoldRegions.toList()

        private fun fold() {
            originalFoldRegions[editor] = allFoldRegions

            val zenifyVisitor = ZenifyVisitor(AppService.instance.settings)
            psiFile.accept(zenifyVisitor)

            foldingModel.runBatchFoldingOperation {
                removeAllFoldRegions()
                zenifyVisitor.foldings.forEach { fold(it) }
            }
            zenifiedFoldRegions[editor] = allFoldRegions
        }

        private fun fold(region: ZenifiedFoldRegion) {
            val textLength = editor.document.textLength
            if (region.startOffset < textLength && region.endOffset < textLength) {
                foldingModel.addFoldRegion(region.startOffset, region.endOffset, region.placeholderText)?.let {
                    it.isExpanded = false
                    it.setInnerHighlightersMuted(false)
                    it.isGutterMarkEnabledForSingleLine = false
                }
            }
        }

        private fun unfold() {
            foldingModel.runBatchFoldingOperation {
                removeAllFoldRegions()
                originalFoldRegions[editor]?.forEach { unfold(it) }
            }
            zenifiedFoldRegions.remove(editor)
        }

        private fun unfold(region: FoldRegion) {
            foldingModel.addFoldRegion(region.startOffset, region.endOffset, region.placeholderText)?.let {
                it.isExpanded = region.isExpanded
                it.setInnerHighlightersMuted(region.areInnerHighlightersMuted())
                it.isGutterMarkEnabledForSingleLine = region.isGutterMarkEnabledForSingleLine
            }
        }

        private fun removeAllFoldRegions() {
            allFoldRegions.forEach {
                foldingModel.removeFoldRegion(it)
            }
        }

    }

}
