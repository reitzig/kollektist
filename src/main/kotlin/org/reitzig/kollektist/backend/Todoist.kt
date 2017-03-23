package org.reitzig.kollektist.backend

import com.github.kittinunf.fuel.core.FuelManager
import org.reitzig.kollektist.*
import org.reitzig.kollektist.backend.Backend

/**
 * Refer to https://developer.todoist.com/ for documentation.
 * TODO document
 * @author Raphael Reitzig
 */
object Todoist: Backend {

    private val baseUrl = "https://todoist.com/API/v7/sync"

    override fun labels(): Set<Label> {
        return setOf(Label("Test Label"))
    }

    override fun projects(): Set<Project> {
        return setOf(Project("Test Project"))
    }

    override fun add(task: Task) {
        println("Stored task $task")
    }
}