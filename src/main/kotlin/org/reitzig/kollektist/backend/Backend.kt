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

import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

/**
 * Abstraction for Kollektist backends.
 *
 * Backends retrieve lists of labels and projects (maybe empty) and
 * write tasks.
 */
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