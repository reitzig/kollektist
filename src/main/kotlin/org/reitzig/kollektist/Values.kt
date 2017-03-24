package org.reitzig.kollektist

// TODO document

data class Task(val description: String, val project: Project, val labels: Set<Label>, val priority: Priority)
data class Label(val name: String)
data class Project(val name: String)

enum class Priority(val numeric: Int) {
    Critical(1),
    High(2),
    Normal(3),
    Low(4);

    companion object {
        // From http://stackoverflow.com/a/37795810/539599
        private val map = Priority.values().associateBy(Priority::numeric);
        fun valueOf(numeric: Int) = map[numeric]
    }
}