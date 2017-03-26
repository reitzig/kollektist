package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
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
            return labelsFile.readLines().map { Label(it) }.toSet()
        } else {
            return setOf()
        }
    }

    override fun projects(): Set<Project> {
        if (projectsFile.exists()) {
            return projectsFile.readLines().map { Project(it) }.toSet()
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
            it.appendln()
            it.write(task.priority.numeric.toString())
        }
    }

    override fun next(): Task? {
        val files = directory.walkTopDown().maxDepth(1).filter { it.name.startsWith(taskFilePrefix) }
        return files.firstOrNull()?.let {
            val lines = it.readLines()
            it.delete()

            if (lines.isNotEmpty()) { // TODO same as CLI; abstract out?
                Task(lines[0],
                     Project(lines.getOrNull(1) ?: "Inbox"), // TODO general?
                     lines.getOrNull(2)?.split(",")?.map { Label(it) }?.toSet() ?: setOf(),
                     lines.getOrNull(3)?.toIntOrNull()?.let { Priority.valueOf(it) } ?: Priority.Normal
                )
            } else {
                null
            }
        }
    }
}