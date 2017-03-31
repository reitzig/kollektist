package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.Task
import org.reitzig.kollektist.backend.Backend

interface Frontend {
    fun prepare(target: Backend)
    fun next(): Task?
}