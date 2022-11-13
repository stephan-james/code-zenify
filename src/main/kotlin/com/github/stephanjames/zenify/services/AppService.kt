package com.github.stephanjames.zenify.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "com.github.stephanjames.zenify",
    storages = [Storage("ZenifyPlugin.xml")])
class AppService : PersistentStateComponent<DefaultAppSettings> {

    val settings = DefaultAppSettings()

    override fun getState() =
        settings

    override fun loadState(state: DefaultAppSettings) {
        getState().apply(state)
    }

    companion object {
        val instance: AppService
            get() = ApplicationManager.getApplication().getService(AppService::class.java)
    }
}
