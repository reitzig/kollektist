package org.reitzig.kollektist

// TODO document

data class Task(val description: String, val project: Project, val labels: Set<Label>, val priority: Priority)
data class Label(val name: String, val color: Color = Color.NoColor)
data class Project(val name: String, val color: Color = Color.NoColor)

enum class Priority(val numeric: Int, val color: Color = Color.NoColor) {
    Critical(1, Color(172, 0, 0)),
    High(2, Color(248, 128, 28)),
    Urgent(3, Color(252, 193, 43)),
    Normal(4);

    companion object {
        // From http://stackoverflow.com/a/37795810/539599
        private val map = Priority.values().associateBy(Priority::numeric);
        fun valueOf(numeric: Int) = map[numeric]
    }
}

data class Color(val r: Int, val g: Int, val b: Int) {
    constructor(hex: String) : this(hexExtract(hex, 1..2), hexExtract(hex, 3..4), hexExtract(hex, 5..6))

    fun distance(other: Color): Double {
        return Math.sqrt(Math.pow((r - other.r).toDouble(), 2.0) +
                         Math.pow((g - other.g).toDouble(), 2.0) +
                         Math.pow((b - other.b).toDouble(), 2.0))
    }

    companion object {
        /**
         * A dummy color that indicates that no special color should be used.
         */
        val NoColor = Color(-77,-77,-77)

        fun hexExtract(input: String, range: IntRange): Int {
            assert(Regex("#[a-fA-F0-6]{6}").matches(input), {"$input is not a hex color"})
            return input.substring(range).toInt(16)
        }
    }
}