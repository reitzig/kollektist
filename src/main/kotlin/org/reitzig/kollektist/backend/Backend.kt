package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

/**
 * Created by raphael on 22.03.17.
 */
interface Backend {
    fun labels(): Set<Label>
    fun projects(): Set<Project>
    fun add(task: Task)
}