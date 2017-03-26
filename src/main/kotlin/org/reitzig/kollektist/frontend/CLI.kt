package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.*

object CLI: Frontend {
    override fun next(): Task? {
        print("Task name: ")
        val name = readLine() ?: return this.next()
        print("Project: ")
        val project = Project(readLine() ?: "Inbox") // TODO universally right?
        print("Labels: ")
        val labels = readLine()?.split(Regex("[\\s,]+"))?.map { Label(it) } ?: listOf()
        print("Priority: ")
        val priority: Priority = readLine()?.trim()?.toIntOrNull()?.let { Priority.valueOf(it) } ?: Priority.Normal

        return Task(name, project, labels.toSet(), priority)
    }
}