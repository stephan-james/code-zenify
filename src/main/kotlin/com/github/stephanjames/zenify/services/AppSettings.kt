package com.github.stephanjames.zenify.services

interface AppSettings {

    var foldBlocks: Boolean
    var foldComments: Boolean
    var foldFieldTypeElement: Boolean
    var foldGettersSetters: Boolean
    var foldImportLists: Boolean
    var foldLocalVariableTypeElement: Boolean
    var foldLogs: Boolean
    var foldMethods: List<String>
    var foldModifierList: Boolean
    var foldParameterTypeElement: Boolean
    var foldParentheses: Boolean
    var foldSemicolons: Boolean
    var foldThrowsStatement: Boolean
    var foldTypeParameterList: Boolean

    fun isEqualTo(that: AppSettings?): Boolean = that != null
            && this.foldBlocks == that.foldBlocks
            && this.foldComments == that.foldComments
            && this.foldFieldTypeElement == that.foldFieldTypeElement
            && this.foldGettersSetters == that.foldGettersSetters
            && this.foldImportLists == that.foldImportLists
            && this.foldLocalVariableTypeElement == that.foldLocalVariableTypeElement
            && this.foldLogs == that.foldLogs
            && this.foldMethods == that.foldMethods
            && this.foldModifierList == that.foldModifierList
            && this.foldParameterTypeElement == that.foldParameterTypeElement
            && this.foldParentheses == that.foldParentheses
            && this.foldSemicolons == that.foldSemicolons
            && this.foldThrowsStatement == that.foldThrowsStatement
            && this.foldTypeParameterList == that.foldTypeParameterList

    fun apply(that: AppSettings) {
        this.foldBlocks = that.foldBlocks
        this.foldComments = that.foldComments
        this.foldFieldTypeElement = that.foldFieldTypeElement
        this.foldGettersSetters = that.foldGettersSetters
        this.foldImportLists = that.foldImportLists
        this.foldLocalVariableTypeElement = that.foldLocalVariableTypeElement
        this.foldLogs = that.foldLogs
        this.foldMethods = that.foldMethods.toMutableList()
        this.foldModifierList = that.foldModifierList
        this.foldParameterTypeElement = that.foldParameterTypeElement
        this.foldParentheses = that.foldParentheses
        this.foldSemicolons = that.foldSemicolons
        this.foldThrowsStatement = that.foldThrowsStatement
        this.foldTypeParameterList = that.foldTypeParameterList
    }

}
