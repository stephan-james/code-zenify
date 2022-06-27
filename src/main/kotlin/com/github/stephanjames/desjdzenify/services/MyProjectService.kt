package com.github.stephanjames.desjdzenify.services

import com.intellij.openapi.project.Project
import com.github.stephanjames.desjdzenify.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
