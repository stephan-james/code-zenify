package com.github.stephanjames.zenify.settings

import com.github.stephanjames.zenify.services.AppService
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent


class AppSettingsConfigurable : Configurable {

    private var component: AppSettingsComponent? = null

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Code Zenify Settings"
    }

    override fun createComponent(): JComponent? {
        component = AppSettingsComponent()
        return component?.panel
    }

    override fun isModified() =
        !settings.isEqualTo(component)

    override fun apply() {
        component?.let { settings.apply(it) }
    }

    override fun reset() {
        component?.apply(settings)
    }

    override fun disposeUIResources() {
        component = null
    }

    private val settings =
        AppService.instance.settings
}
