package org.reitzig.kollektist.backend

import com.github.salomonbrys.kotson.typeToken
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.frontend.Frontend
import java.io.File

class Files(dirPath: String) : Backend, Frontend {
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