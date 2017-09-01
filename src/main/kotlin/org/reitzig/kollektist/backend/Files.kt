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

package org.reitzig.kollektist.backend

import com.github.salomonbrys.kotson.typeToken
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.frontend.Frontend
import java.io.File

/**
 * A file-based backend *and* frontend.
 *
 * As a frontend, it reads tasks from files stored in a user-set directory.
 * Only files whose names start with `task_` are considered (in filesystem order),
 * and their content has to be JSON compatible with `taskAdapter`.
 * It also writes files with projects and labels, if the used backend provides any.
 *
 * As a backend, it writes task to files in a user-set directory.
 * It also reads projects and labels from files, if there are any.
 *
 * Obviously, the two modes are compatible. You could have one instance of Kollektist with
 * frontend `Files` and backend `Todoist` looping to pick up new tasks created by
 * any frontend together with *backend* `Files.
 */
class Files(dirPath: String) : Backend, Frontend {
    /**
     * The folder this instance will store files in.
     */
    private var directory: File

    val labelsFile: File
    val projectsFile: File
    val taskFilePrefix = "task_"

    init {
        val dir = File(dirPath)
        if (dir.isDirectory && dir.canWrite()) {
            this.directory = dir
            this.labelsFile = File("${directory.absolutePath}/labels")
            this.projectsFile = File("${directory.absolutePath}/projects")
        } else {
            throw IllegalArgumentException("'$dir' is not a writable directory")
        }
    }

    override fun labels(): List<Label> {
        if (labelsFile.exists()) {
            return JsonHandler.fromJson(labelsFile.readText(), typeToken<List<Label>>())
        } else {
            return listOf()
        }
    }

    override fun projects(): List<Project> {
        if (projectsFile.exists()) {
            return JsonHandler.fromJson(projectsFile.readText(), typeToken<List<Project>>())
        } else {
            return listOf()
        }
    }

    override fun add(task: Task) {
        // TODO pick more robust way of choosing filename
        val target = File("${directory.absolutePath}/$taskFilePrefix${System.currentTimeMillis()}")

        // Prevent Files.next picking up anything before file is written completely
        val tmp = File.createTempFile("kollektist", "")
        tmp.writer().use {
            it.write(task.toJson())
        }
        tmp.copyTo(target)
        tmp.delete()
    }

    override fun prepare(target: Backend) {
        // Chances are somebody is (going to be) running Files as backend
        // -- otherwise this instance would be pretty useless!
        // So, write out the project and label list

        val labels = target.labels()
        if (!labels.isEmpty()) {
            labelsFile.writeText(JsonHandler.toJson(labels))
        }

        val projects = target.projects()
        if (!projects.isEmpty()) {
            projectsFile.writeText(JsonHandler.toJson(projects))
        }

        // TODO how/when to update the files? After every call to next?
    }

    override fun next(): Task? {
        val files = directory.walkTopDown().maxDepth(1).filter { it.name.startsWith(taskFilePrefix) }
        return files.firstOrNull()?.let {
            val content = it.readText()
            it.delete()
            return Task(json = content)
        }
    }
}