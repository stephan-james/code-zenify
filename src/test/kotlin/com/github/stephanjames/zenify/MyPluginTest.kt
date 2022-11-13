package com.github.stephanjames.zenify

import com.github.stephanjames.zenify.services.DefaultAppSettings
import com.github.stephanjames.zenify.visitor.ZenifyVisitor
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun test() {
        val psiFile = myFixture.configureByFile("Test.java")

        psiFile.accept(ZenifyVisitor(DefaultAppSettings()))
    }

    override fun getTestDataPath() = "src/test/testData"

}
