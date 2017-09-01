/*
    Copyright (c) Raphael Reitzig 2017

    This file is part of Kollektist.

    Kollektist is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Kollektist is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Kollektist.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.backend.Backend
import org.reitzig.kollektist.backend.Echo
import org.reitzig.kollektist.backend.Todoist

/**
 * A command-line frontend.
 * Presents the user with a basic series of prompts for the properties of the new task.
 *
 *  * Project and label names must match exactly.
 *  * Enter project and label names without `@` and `#`.
 *  * Separate multiple labels by comma and/or whitespace.
 *  * Priority must be one of `4` (highest), `3`, `2`, `1` (lowest).
 *
 *  The defaults are `Inbox`, no label, `1`.
 */
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