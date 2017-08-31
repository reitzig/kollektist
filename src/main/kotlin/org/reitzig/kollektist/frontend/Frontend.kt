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