package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.backend.Backend
import org.reitzig.kollektist.backend.Echo
import org.reitzig.kollektist.backend.Todoist

object CLI: Frontend {
    private var backend: Backend? = null

    override fun prepare(target: Backend) {
        this.backend = target

        println("Available projects: ")
        println(target.projects().map {
            Echo.AnsiColor.wrap(it.name, Todoist.Colors[it.color])
        }.joinToString(", ")
        )
        println("Available labels: ")
        println(target.labels().map {
            Echo.AnsiColor.wrap(it.name, Todoist.Colors[it.color])
        }.joinToString(", ")
        )
    }

    override fun next(): Task? {
        print("Task name: ")
        val name = readLine() ?: return this.next()

        var project: Project? = null
        val labels: MutableSet<Label> = mutableSetOf()
        if (this.backend != null) {
            print("Project: ")
            val projectName = readLine()
            project = this.backend!!.projects().firstOrNull() { it.name == projectName } // TODO fuzzy search?
            println("\tAdding task to #${project?.name ?: "Inbox"}") // TODO or create new?

            print("Labels: ")
            readLine()?.split(Regex("[\\s,]+"))?.forEach { labelName ->
                this.backend!!.labels().firstOrNull() { it.name == labelName }?.let { labels.add(it) }
            }  // TODO fuzzy search?
            println("\tAssigning labels ${labels.map { "@${it.name}" }.joinToString(", ")}") // TODO or create new?
        }
        print("Priority: ")
        val priority: Priority = readLine()?.trim()?.toIntOrNull()?.let { Priority(it) } ?: Priority.Normal

        return Task(name, project, labels.toSet(), priority)
    }
}