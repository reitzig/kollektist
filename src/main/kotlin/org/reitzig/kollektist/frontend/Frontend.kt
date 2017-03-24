package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.*

interface Frontend {
    fun next(): Task?
}