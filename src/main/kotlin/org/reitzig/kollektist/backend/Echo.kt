package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

object Echo: Backend {
    override fun labels(): Set<Label> {
        return setOf()
    }

    override fun projects(): Set<Project> {
        return setOf()
    }

    override fun add(task: Task) {
        println(task.description)
        println("#${task.project.name}")
        println(task.labels.map { "@${it.name}" }.joinToString(" "))
    }
}