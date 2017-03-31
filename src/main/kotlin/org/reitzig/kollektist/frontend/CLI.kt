package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.backend.Backend
import org.reitzig.kollektist.backend.Echo
import org.reitzig.kollektist.backend.Todoist

object CLI: Frontend {
    override fun prepare(target: Backend) {
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
        print("Project: ")
        val project = Project(readLine() ?: "Inbox") // TODO universally right?
        print("Labels: ")
        val labels = readLine()?.split(Regex("[\\s,]+"))?.map { Label(it) } ?: listOf()
        print("Priority: ")
        val priority: Priority = readLine()?.trim()?.toIntOrNull()?.let { Priority(it) } ?: Priority.Normal

        return Task(name, project, labels.toSet(), priority)
    }
}