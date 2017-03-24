package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

interface Backend {
    /**
     * Returns the set of labels that is available on this backend.
     */
    fun labels(): Set<Label>

    /**
     * Returns the set of projects that is available on this backend.
     */
    fun projects(): Set<Project>

    /**
     * Adds a task via this backend.
     */
    fun add(task: Task)
}