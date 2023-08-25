package com.github.stephanjames.zenify.services

class DefaultAppSettings : AppSettings {

    override var foldComments = true
    override var foldSemicolons = true
    override var foldParentheses = true
    override var foldBlocks = true
    override var foldModifierList = true
    override var foldFieldTypeElement = true
    override var foldParameterTypeElement = true
    override var foldLocalVariableTypeElement = true
    override var foldTypeParameterList = true
    override var foldThrowsStatement = true
    override var foldGettersSetters = true
    override var foldImportLists = true
    override var foldLogs = true
    override var foldNewKeyword = true
    override var foldMethods = mutableListOf(
        "equals(java.lang.Object) -> boolean",
        "hashCode() -> int",
        "toString() -> java.lang.String"
    ) as List<String>

}
