package org.reitzig.kollektist.backend

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

    override fun labels(): Set<Label> {
        if (labelsFile.exists()) {
            return labelsFile.readLines().map(::Label).toSet()
        } else {
            return setOf()
        }
    }

    override fun projects(): Set<Project> {
        if (projectsFile.exists()) {
            return projectsFile.readLines().map(::Project).toSet()
        } else {
            return setOf()
        }
    }

    override fun add(task: Task) {
        // TODO pick more robust way of choosing filename
        File("${directory.absolutePath}/$taskFilePrefix${System.currentTimeMillis()}").writer().use {
            it.write(task.description)
            it.appendln()
            it.write(task.project.name)
            it.appendln()
            it.write(task.labels.map { it.name }.joinToString(","))
        }
    }

    override fun next(): Task? {
        val files = directory.walkTopDown().maxDepth(1).filter { it.name.startsWith(taskFilePrefix) }
        return files.firstOrNull()?.let {
            val lines = it.readLines()
            it.delete()

            if (lines.size >= 3) {
                Task(lines[0], Project(lines[1]), lines[2].split(",").map(::Label).toSet())
            } else {
                null
            }
        }
    }
}