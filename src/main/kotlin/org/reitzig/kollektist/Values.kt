package org.reitzig.kollektist

import com.github.salomonbrys.kotson.fromJson
import org.reitzig.kollektist.backend.JsonHandler

// TODO document
// TODO use a map delegate instead?
//      https://kotlinlang.org/docs/reference/delegated-properties.html#storing-properties-in-a-map

data class Task(val description: String,
                val project: Project, // = Inbox?!
                val labels: Set<Label> = setOf(),
                val priority: Priority = Priority.Normal) : JsonRepresentable<Task> {
    override fun toJson(): String {
        return JsonHandler.toJson(this)
    }

    companion object : JsonRepresentable.Companion<Task> {
        override operator fun invoke(json: String): Task? {
            return JsonHandler.fromJson<Task>(json)
        }
    }
}

data class Label(val name: String,
                 val id: Int = System.nanoTime().toInt(), // a temp id!
                 val color: Int? = null,
                 val itemOrder: Int? = null,
                 val isObsolete: Boolean = false) : JsonRepresentable<Label> {
    init {
        assert(color == null || color in 0..22, { "$color is not a Todoist color code" })
    }

    override fun toJson(): String {
        return JsonHandler.toJson(this)
    }

    companion object : JsonRepresentable.Companion<Label> {
        override operator fun invoke(json: String): Label? {
            return JsonHandler.fromJson<Label>(json)
        }
    }
}

data class Project(val name: String,
                   val id: Int = System.nanoTime().toInt(), // a temp id!
                   val color: Int? = null,
                   val itemOrder: Int? = null,
                   val parent: Int? = null,
                   val indent: Int? = null,
                   val isObsolete: Boolean = false) : JsonRepresentable<Project> {
    init {
        assert(color == null || color in 0..22, { "$color is not a Todoist color code" })
    }

    override fun toJson(): String {
        return JsonHandler.toJson(this)
    }

    companion object : JsonRepresentable.Companion<Project> {
        override operator fun invoke(json: String): Project? {
            return JsonHandler.fromJson<Project>(json)
        }
    }
}

enum class Priority(val numeric: Int, val color: Color = Color.NoColor) {
    Critical(1, Color(172, 0, 0)),
    High(2, Color(248, 128, 28)),
    Urgent(3, Color(252, 193, 43)),
    Normal(4);

    companion object {
        // From http://stackoverflow.com/a/37795810/539599
        private val map = Priority.values().associateBy(Priority::numeric);

        operator fun invoke(numeric: Int) = map[numeric]
        operator fun invoke(name: String) = Priority.valueOf(name)
    }
}

interface JsonRepresentable<Self : JsonRepresentable<Self>> {
    fun toJson(): String

    interface Companion<Outer : JsonRepresentable<Outer>> {
        operator fun invoke(json: String): Outer?
    }
}