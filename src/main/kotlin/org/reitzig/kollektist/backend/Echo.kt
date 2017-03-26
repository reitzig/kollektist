package org.reitzig.kollektist.backend

import org.reitzig.kollektist.Color
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

object Echo : Backend {
    override fun labels(): Set<Label> {
        return setOf()
    }

    override fun projects(): Set<Project> {
        return setOf()
    }

    override fun add(task: Task) {
        println(task.description)
        println("#${AnsiColor.wrap(task.project.name, task.project.color)}")
        println(task.labels.map { "@${AnsiColor.wrap(it.name, it.color)}" }.joinToString(" "))
        println(AnsiColor.wrap("${task.priority.name} priority", task.priority.color))
    }

    private

    enum class AnsiColor(val ansi: String, val rgb: Color) {
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
            fun closestTo(color: Color): AnsiColor {
                return values().map { Pair(it, it.rgb.distance(color)) }
                        .minBy { it.second }!!
                        .first
            }

            fun wrap(string: String, color: Color): String {
                return "${AnsiColor.closestTo(color).shellCode}$string${AnsiColor.NoColor.shellCode}"
            }
        }
    }
}