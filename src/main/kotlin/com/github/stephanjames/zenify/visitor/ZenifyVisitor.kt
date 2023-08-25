package com.github.stephanjames.zenify.visitor

import com.github.stephanjames.zenify.services.AppSettings
import com.intellij.psi.*

class ZenifiedFoldRegion(val startOffset: Int, val endOffset: Int, val placeholderText: String = "")

// TODO correct whitespace on catch clause

class ZenifyVisitor(private val settings: AppSettings) : JavaRecursiveElementVisitor() {

    val foldings = mutableListOf<ZenifiedFoldRegion>()

    override fun visitComment(element: PsiComment) {
        if (settings.foldComments) {
            foldElement(element)
            foldNextWhiteSpace(element)
            return
        }

        super.visitComment(element)
    }

    override fun visitBlockStatement(element: PsiBlockStatement) {
        super.visitBlockStatement(element)

        if (settings.foldBlocks) {
            foldPreviousWhiteSpace(element)
        }
    }

    override fun visitCodeBlock(element: PsiCodeBlock) {
        super.visitCodeBlock(element)

        if (settings.foldBlocks) {
            foldElement(element.children.first())
            foldElement(element.children.last())
            if (hasFoldableWhiteSpace(element)) {
                foldPreviousWhiteSpace(element.children.last())
            }
        }
    }

    private fun hasFoldableWhiteSpace(element: PsiCodeBlock): Boolean {
        val grandParent = element.parent?.parent
        if (grandParent is PsiIfStatement) {
            if (grandParent.elseBranch == null) {
                return true
            }
            if (grandParent.thenBranch == element.parent) {
                return false
            }
            return true
        }
        return true
    }

    override fun visitJavaToken(element: PsiJavaToken) {
        if (settings.foldSemicolons) {
            if (element.tokenType == JavaTokenType.SEMICOLON
                && element.parent !is PsiForStatement
                && element.parent?.parent?.parent !is PsiForStatement
            ) {
                foldElement(element, "")
                return
            }
        }

        if (settings.foldParentheses && isParenthesis(element) && isControlStructure(element.parent)) {
            foldElement(element, "")
            return
        }

        super.visitJavaToken(element)
    }

    private fun isControlStructure(element: PsiElement) =
        (element is PsiIfStatement
                || element is PsiWhileStatement
                || element is PsiDoWhileStatement
                || element is PsiForStatement)

    private fun isParenthesis(element: PsiJavaToken) =
        element.tokenType == JavaTokenType.LPARENTH || element.tokenType == JavaTokenType.RPARENTH

    override fun visitKeyword(element: PsiKeyword) {
        if (settings.foldThrowsStatement && element.tokenType == JavaTokenType.THROWS_KEYWORD) {
            foldElement(element.parent)
            return
        }

        if(settings.foldNewKeyword && element.tokenType == JavaTokenType.NEW_KEYWORD && element.parent is PsiNewExpression) {
            foldElement(element)
            return
        }

        super.visitKeyword(element)

        if (!settings.foldBlocks) {
            return
        }

        if (element.tokenType == JavaTokenType.ELSE_KEYWORD) {
            if (element.prevSibling is PsiWhiteSpace && element.prevSibling?.prevSibling is PsiBlockStatement) {
                foldPreviousWhiteSpace(element)
            }
            if (element.nextSibling is PsiWhiteSpace && element.nextSibling?.nextSibling is PsiBlockStatement) {
                foldNextWhiteSpace(element)
            }
        }
        if (element.tokenType == JavaTokenType.WHILE_KEYWORD) {
            if (element.prevSibling is PsiWhiteSpace && element.prevSibling?.prevSibling is PsiBlockStatement) {
                foldPreviousWhiteSpace(element)
            }
        }
    }

    override fun visitModifierList(element: PsiModifierList) {
        if (settings.foldModifierList) {
            foldElement(element)
            foldNextWhiteSpace(element)
            return
        }

        super.visitModifierList(element)
    }

    override fun visitTypeElement(element: PsiTypeElement) {
        if ((element.parent is PsiField && settings.foldFieldTypeElement)
            || (element.parent is PsiParameter && settings.foldParameterTypeElement)
            || (element.parent is PsiLocalVariable && settings.foldLocalVariableTypeElement)
        ) {
            foldElement(element)
            foldNextWhiteSpace(element)
            return
        }

        super.visitTypeElement(element)
    }

    override fun visitTypeParameterList(element: PsiTypeParameterList) {
        if (settings.foldTypeParameterList) {
            foldElement(element)
            return
        }

        super.visitTypeParameterList(element)
    }

    override fun visitReferenceParameterList(element: PsiReferenceParameterList) {
        if (settings.foldTypeParameterList) {
            foldElement(element)
            return
        }

        super.visitReferenceParameterList(element)
    }

    override fun visitImportList(element: PsiImportList) {
        if (settings.foldImportLists) {
            foldElement(element)
            return
        }

        super.visitImportList(element)
    }

    override fun visitField(element: PsiField) {
        if (settings.foldLogs
            && firstMethodCallExpressionMatches(element.children, "Logger.*?\\..*getLogger.*\\(.*\\).*")
        ) {
            foldElement(element)
            return
        }

        super.visitField(element)
    }

    private fun firstMethodCallExpressionMatches(elements: Array<out PsiElement>, pattern: String): Boolean {
        val firstMethodCallExpression = elements.firstOrNull { it is PsiMethodCallExpression }
        return firstMethodCallExpression != null && firstMethodCallExpression.text.matches(
            Regex(pattern, setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
        )
    }

    override fun visitMethodCallExpression(element: PsiMethodCallExpression) {
        if (settings.foldLogs && textMatches(element, "LOG\\w.*\\.(trace|debug|info|warn|error|fatal|fine).*?\\(.*\\).*")) {
            foldElement(element, "log(…)")
            return
        }

        super.visitMethodCallExpression(element)
    }

    private fun textMatches(element: PsiElement, pattern: String): Boolean {
        val regex = Regex(pattern, setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
        return element.text.matches(regex)
    }

    override fun visitMethod(element: PsiMethod) {
        if (settings.foldMethods.contains(canonicalText(element))) {
            foldElement(element)
            return
        }

        if (settings.foldGettersSetters && (isGetter(element) || isSetter(element))) {
            foldElement(element)
            return
        }

        super.visitMethod(element)
    }

    private fun isGetter(element: PsiMethod): Boolean {
        // TODO check for methode name prefix and identifiers
        if (element.returnType == PsiType.VOID || element.parameterList.parametersCount > 0 || element.body == null) {
            return false
        }

        val returnStatement = importantChildren(element.body!!.children)
        // TODO more robustness
        if (returnStatement is PsiReturnStatement) {
            val referenceExpression = importantReturnedChildren(returnStatement.children)
            if (referenceExpression is PsiReferenceExpression) {
                val referencedChildren = importantReferencedChildren(referenceExpression.children)
                if (referencedChildren != null && referencedChildren.children.isEmpty()) {
                    return true
                }
            }
        }
        return false
    }

    private fun isSetter(element: PsiMethod) =
        element.name.startsWith("set")
                && element.returnType == PsiType.VOID
                && element.parameterList.parametersCount == 1
                && element.body != null
                && isAssignmentExpression(element.body!!.children)

    private fun isAssignmentExpression(elements: Array<out PsiElement>): Boolean {
        val expressionStatement = importantChildren(elements)
        return expressionStatement is PsiExpressionStatement && expressionStatement.expression is PsiAssignmentExpression
    }

    private fun canonicalText(element: PsiMethod) =
        "${element.name}(${canonicalText(element.parameterList.parameters)})${canonicalText(element.returnType)}"

    private fun canonicalText(parameters: Array<out PsiParameter>) =
        parameters.joinToString(",") { canonicalText(it) }

    private fun canonicalText(parameter: PsiParameter) =
        (parameter.children.find { it is PsiTypeElement } as PsiTypeElement).type.canonicalText

    private fun canonicalText(type: PsiType?) =
        if (type != null) {
            " -> ${type.canonicalText}"
        } else {
            ""
        }

    private fun foldPreviousWhiteSpace(element: PsiElement) {
        if (element.prevSibling is PsiWhiteSpace) {
            foldElement(element.prevSibling)
        }
    }

    private fun foldNextWhiteSpace(element: PsiElement) {
        if (element.nextSibling is PsiWhiteSpace) {
            foldElement(element.nextSibling)
        }
    }

    private fun foldElement(element: PsiElement, placeholderText: String = "") {
        val foldRegion = ZenifiedFoldRegion(element.textRange.startOffset, element.textRange.endOffset, placeholderText)
        foldings.add(foldRegion)
    }

    private fun importantChildren(children: Array<out PsiElement>) =
        toOne(children.filter { it !is PsiComment }.filter { it !is PsiWhiteSpace }.filter { it !is PsiJavaToken })

    private fun importantReturnedChildren(children: Array<out PsiElement>) =
        toOne(children.filter { it !is PsiComment }.filter { it !is PsiWhiteSpace }.filter { it !is PsiJavaToken }
            .filter { it !is PsiKeyword })

    private fun importantReferencedChildren(children: Array<out PsiElement>) =
        toOne(children.filter { it !is PsiComment }.filter { it !is PsiWhiteSpace }.filter { it !is PsiThisExpression }
            .filter { it !is PsiJavaToken })

    private fun toOne(children: List<PsiElement>) =
        if (children.size == 1) {
            children.first()
        } else {
            null
        }

}
