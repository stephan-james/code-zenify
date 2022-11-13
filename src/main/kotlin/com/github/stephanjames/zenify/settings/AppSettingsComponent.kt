package com.github.stephanjames.zenify.settings

import com.github.stephanjames.zenify.services.AppSettings
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI.PanelFactory
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea
import kotlin.reflect.KProperty

class AppSettingsComponent : AppSettings {

    val panel: JPanel

    private val uiFoldBlocks = JBCheckBox("<html>Fold braces of block statements</html>")
    private val uiFoldComments = JBCheckBox("<html>Fold comments</html>")
    private val uiFoldFieldTypeElement = JBCheckBox("<html>Fold field type elements</html>")
    private val uiFoldGettersSetters = JBCheckBox("<html>Fold getters &amp; setters</html>")
    private val uiFoldImportLists = JBCheckBox("<html>Fold import lists</html>")
    private val uiFoldLocalVariableTypeElement = JBCheckBox("<html>Fold local variable type elements</html>")
    private val uiFoldLogs = JBCheckBox("<html>Fold log declarations and statements</html>")
    private val uiFoldModifierList = JBCheckBox("<html>Fold modifier lists</html>")
    private val uiFoldParameterTypeElement = JBCheckBox("<html>Fold parameter type elements</html>")
    private val uiFoldParentheses = JBCheckBox("<html>Fold surrounding parentheses of conditional expressions</html>")
    private val uiFoldSemicolons = JBCheckBox("<html>Fold semicolons</html>")
    private val uiFoldThrowsStatement = JBCheckBox("<html>Fold throws clauses</html>")
    private val uiFoldTypeParameterList = JBCheckBox("<html>Fold type parameter lists</html>")
    private val uiFoldMethods = JBTextArea(10, 80)

    init {
        uiFoldMethods.margin = Insets(10, 10, 10, 10)
        panel = FormBuilder.createFormBuilder()
            .addComponent(uiFoldComments, 1)
            .addComponent(uiFoldParentheses, 1)
            .addComponent(uiFoldBlocks, 1)
            .addComponent(uiFoldSemicolons, 1)
            .addComponent(uiFoldModifierList, 1)
            .addComponent(uiFoldFieldTypeElement, 1)
            .addComponent(uiFoldParameterTypeElement, 1)
            .addComponent(uiFoldLocalVariableTypeElement, 1)
            .addComponent(uiFoldTypeParameterList, 1)
            .addComponent(uiFoldThrowsStatement, 1)
            .addComponent(uiFoldGettersSetters, 1)
            .addComponent(uiFoldImportLists, 1)
            .addComponent(uiFoldLogs, 1)
            .addComponent(buildLabelFoldMethods(), 10)
            .addComponent(uiFoldMethods, 4)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun buildLabelFoldMethods() = PanelFactory
        .panel(JBLabel("<html>Methods to fold</html>"))
        .withComment("<html>List method definitions. One per line, e.g. <em>equals(java.lang.Object) -> boolean<em></html>")
        .createPanel()

    override var foldBlocks: Boolean by BooleanValue(uiFoldBlocks)
    override var foldComments: Boolean by BooleanValue(uiFoldComments)
    override var foldFieldTypeElement: Boolean by BooleanValue(uiFoldFieldTypeElement)
    override var foldGettersSetters: Boolean by BooleanValue(uiFoldGettersSetters)
    override var foldImportLists: Boolean by BooleanValue(uiFoldImportLists)
    override var foldLocalVariableTypeElement: Boolean by BooleanValue(uiFoldLocalVariableTypeElement)
    override var foldLogs: Boolean by BooleanValue(uiFoldLogs)
    override var foldMethods: List<String> by FoldMethodsValue(uiFoldMethods)
    override var foldModifierList: Boolean by BooleanValue(uiFoldModifierList)
    override var foldParameterTypeElement: Boolean by BooleanValue(uiFoldParameterTypeElement)
    override var foldParentheses: Boolean by BooleanValue(uiFoldParentheses)
    override var foldSemicolons: Boolean by BooleanValue(uiFoldSemicolons)
    override var foldThrowsStatement: Boolean by BooleanValue(uiFoldThrowsStatement)
    override var foldTypeParameterList: Boolean by BooleanValue(uiFoldTypeParameterList)

    class BooleanValue(private val component: JCheckBox) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            component.isSelected

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            component.isSelected = value
        }

    }

    class FoldMethodsValue(private val component: JTextArea) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): List<String> =
            component.text.split("\n")

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: List<String>) {
            component.text = value.joinToString("\n") { it
                    .replace(Regex("\\s+"), "")
                    .replace("->", " -> ")
            }
        }

    }

}
