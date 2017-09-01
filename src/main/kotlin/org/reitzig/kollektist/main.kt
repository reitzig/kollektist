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

package org.reitzig.kollektist

import org.reitzig.kollektist.backend.Backend
import org.reitzig.kollektist.backend.Echo
import org.reitzig.kollektist.backend.Files
import org.reitzig.kollektist.backend.Todoist
import org.reitzig.kollektist.frontend.CLI
import org.reitzig.kollektist.frontend.Frontend

/**
 * The main program.
 *
 * After setup, retrieves task from the specified `Frontend` and adds them to the specified `Backend`.
 * If `--loop` is set, this is iterated until the user aborts.
 *
 * @throws IllegalArgumentException if the parameters were invalid.
 */
fun main(args: Array<String>) {
    // Default values
    var folder = "." // TODO make parameter?
    var backend: Backend = Echo
    var frontend: Frontend = CLI
    var apiToken: String? = null
    var loop = false

    if (args.isEmpty()) {
        println("Usage: java -jar <jar> --api-token=<token> --backend=<echo|files|todoist> --frontend=<cli|files> [--loop]")
        return
    }

    // Consume the parameters
    args.forEach {
        if (it.startsWith("--backend=")) {
            when (it.drop(10)) {
                "echo"    -> backend = Echo
                "files"   -> backend = Files(folder)
                "todoist" -> {
                    if (apiToken == null) {
                        throw IllegalArgumentException("API token must be passed before the backend!")
                    }
                    backend = Todoist(apiToken!!)
                }
                else      -> {
                    throw IllegalArgumentException("No backend '$it' available.")
                }
            }
        } else if (it.startsWith("--frontend=")) {
            when (it.drop(11)) {
                "cli"   -> frontend = CLI
                "files" -> frontend = Files(folder)
            //"gui"   -> frontend = GUI(); // TODO
                else    -> {
                    throw IllegalArgumentException("No frontend '$it' available.")
                }
            }
        } else if (it.startsWith("--api-token=")) {
            apiToken = it.drop(12)
        } else if (it == "--loop") {
            loop = true
        } else {
            println("Ignoring unknown parameter '$it'.")
        }
    }

    // TODO do we allow lazy retrieval of API tokens?
    if (apiToken == null) {
        throw java.lang.IllegalArgumentException("No API token provided!")
    }

    frontend.prepare(backend)
    do {
        frontend.next()?.let { backend.add(it) }
        if (loop) Thread.sleep(100) // TODO Without sleep, it fails with looping Files frontend -- why?
    } while (loop) // TODO how to avoid active looping with Files frontend here?
}
