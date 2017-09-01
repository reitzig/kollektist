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

package org.reitzig.kollektist.frontend

import org.reitzig.kollektist.Task
import org.reitzig.kollektist.backend.Backend

/**
 * A frontend for Kollektist.
 *
 * Frontends retrieve new tasks from their respective sources.
 */
interface Frontend {
    /**
     * Called once before anything else happens (by `main(...)`) so that this frontend can
     * perform its preparations, such as retrieving lists of labels to present the user with.
     */
    fun prepare(target: Backend)

    /**
     * Provides the next task, if any; `null` otherwise.
     */
    fun next(): Task?

    // TODO do we need to get the API token while running?
    // fun requestApiToken(): String
}