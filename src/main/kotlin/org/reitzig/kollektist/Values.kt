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