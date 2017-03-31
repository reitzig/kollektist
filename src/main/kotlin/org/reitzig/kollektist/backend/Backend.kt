package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

interface Backend {
    /**
     * Returns the set of labels that is available on this backend.
     */
    fun labels(): List<Label>

    /**
     * Returns the set of projects that is available on this backend.
     */
    fun projects(): List<Project>

    /**
     * Adds a task via this backend.
     */
    fun add(task: Task)
}