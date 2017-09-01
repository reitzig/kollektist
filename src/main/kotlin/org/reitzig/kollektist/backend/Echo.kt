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

import org.reitzig.kollektist.Color
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

/**
 * A simple backend that just prints the tasks it receives to stdout.
 */
object Echo : Backend {
    /**
     * There are no labels to be had here, so this is always empty.
     */
    override fun labels(): List<Label> {
        return listOf()
    }

    /**
     * There are no projects to be had here, so this is always empty.
     */
    override fun projects(): List<Project> {
        return listOf()
    }

    /**
     * Prints this task to stdout.
     */
    override fun add(task: Task) {
        println(task.description)
        task.project?.let {
            println("#${AnsiColor.wrap(it.name, Todoist.Colors[it.color])}")
        }
        println(task.labels.map { "@${AnsiColor.wrap(it.name, Todoist.Colors[it.color])}" }.joinToString(" "))
        println(AnsiColor.wrap("${task.priority.name} priority", task.priority.color))
    }

    /**
     * An abstraction for the ANSI color codes that we can use to highlight
     * labels and projects on the command line.
     */
    internal enum class AnsiColor(val ansi: String, val rgb: Color) {
        NoColor("0", Color.NoColor),
        Black("0;30", Color(0, 0, 0)),
        Red("0;31", Color(170, 0, 0)),
        Green("0;32", Color(0, 170, 0)),
        Brown("0;33", Color(170, 85, 0)),
        Blue("0;34", Color(0, 0, 170)),
        Magenta("0;35", Color(170, 0, 170)),
        Cyan("0;36", Color(0, 170, 170)),
        Gray("0;37", Color(170, 170, 170)),
        DarkGray("1;30", Color(85, 85, 85)),
        LightRed("1;31", Color(255, 85, 85)),
        LightGreen("1;32", Color(85, 255, 85)),
        Yellow("1;33", Color(255, 255, 85)),
        LightBlue("1;34", Color(85, 85, 255)),
        LightMagenta("1;35", Color(255, 85, 255)),
        LightCyan("1;36", Color(85, 255, 255)),
        White("1;37", Color(255, 255, 255));

        val shellCode = "\u001b[${this.ansi}m"

        companion object {
            /**
             * Finds the ANSI color that is closes to the given color,
             * as defined by `Color.distance(...)`.
             */
            fun closestTo(color: Color): AnsiColor {
                return values().map { Pair(it, it.rgb.distance(color)) }
                        .minBy { it.second }!!
                        .first
            }

            /**
             * Wraps `string` in escape sequences that makes the shell print
             * it in the (closest approximation of) `color`.
             */
            fun wrap(string: String, color: Color): String {
                return "${AnsiColor.closestTo(color).shellCode}$string${AnsiColor.NoColor.shellCode}"
            }
        }
    }
}