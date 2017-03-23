package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.frontend.Frontend
import org.reitzig.kollektist.*

object CLI: Frontend {
    override fun next(): Task? {
        print("Task name: ")
        val name = readLine() ?: return this.next()
        print("Project: ")
        val project = readLine() ?: return this.next()
        print("Labels: ")
        val labels = readLine()?.split(Regex("[\\s,]+")) ?: return this.next()
        return Task(name, Project(project), labels.map(::Label).toSet())
    }
}