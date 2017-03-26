package org.reitzig.kollektist

import org.reitzig.kollektist.backend.Backend
import org.reitzig.kollektist.backend.Echo
import org.reitzig.kollektist.backend.Files
import org.reitzig.kollektist.backend.Todoist
import org.reitzig.kollektist.frontend.CLI
import org.reitzig.kollektist.frontend.Frontend

fun main(args: Array<String>) {
    var folder = "." // TODO make parameter?
    var backend: Backend = Todoist
    var frontend: Frontend = CLI
    var loop = false

    args.forEach {
        if (it.startsWith("--backend=")) {
            when (it.drop(10)) {
                "echo" -> backend = Echo
                "files" -> backend = Files(folder)
                "todoist" -> backend = Todoist
                else -> {
                    throw IllegalArgumentException("No backend '$it' available.")
                }
            }
        } else if (it.startsWith("--frontend=")) {
            when (it.drop(11)) {
                "cli"   -> frontend = CLI
                "files" -> frontend = Files(folder)
                "gui"   -> frontend = CLI // TODO change
                else    -> {
                    throw IllegalArgumentException("No frontend '$it' available.")
                }
            }
        } else if (it == "--loop") {
            loop = true
        } else {
            println("Ignoring unknown parameter '$it'.")
        }
    }

    println(backend.labels())
    println(backend.projects())

    do {
        frontend.next()?.let { backend.add(it) }
        if (loop) Thread.sleep(100) // TODO Without sleep, it fails with looping Files frontend -- why?
    } while (loop) // TODO how to avoid active looping with Files frontend here?
}
