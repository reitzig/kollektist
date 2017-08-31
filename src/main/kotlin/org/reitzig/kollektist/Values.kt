package org.reitzig.kollektist

import com.github.salomonbrys.kotson.fromJson
import org.reitzig.kollektist.backend.JsonHandler

// TODO use a map delegate instead?
//      https://kotlinlang.org/docs/reference/delegated-properties.html#storing-properties-in-a-map

/**
 * A Todoist task.
 *
 * @property description    The main task description, e.g. "Document data classes".
 * @property project        The project the task belongs to, e.g. `#Kollektist`.
 *                          If `null`, it is in the Inbox.
 * @property labels         The labels assigned to this task, e.g. `@next`.
 * @property priority       The priority of this task, e.g. `p2`.
 * @constructor Creates a new instance.
 */
data class Task(val description: String,
                val project: Project? = null, // = Inbox
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

/**
 * A Todoist label.
 *
 * @property name       The label name, e.g. "next" for `@next`.
 * @property id         The Todoist ID of this label.
 * @property color      The color Todoist show this label in. If `null`, no special color has been set.
 *                      If set, needs to be in `[0..22]`.
 * @property itemOrder  The position of this label in the label list. `null` if no ordering exists.
 * @property isObsolete `true` if the label was deleted on Todoist.
 * @constructor Creates a new instance. Note in particular the default value of `id`, which is a temporary ID
 *              Kollektist can use to identify this label in responses from Todoist until the label gets its
 *              proper ID.
 *
 */
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

/**
 * A Todoist project.
 *
 * @property name       The project name, e.g. "Kollektist" for `#Kollektist`.
 * @property id         The Todoist ID of this project.
 * @property color      The color Todoist show this project in. If `null`, no special color has been set.
 *                      If set, needs to be in `[0..22]`.
 * @property itemOrder  The position of this project in the project list resp. its parent project.
 *                      `null` if no ordering exists.
 * @property parent     The ID of the parent of this project. If `null`, this is a top-level project.
 * @property indent     The indentation level of this project. If `null`, no indentation has been set.
 * @property isObsolete `true` if the project was deleted or archived on Todoist.
 * @constructor Creates a new instance. Note in particular the default value of `id`, which is a temporary ID
 *              Kollektist can use to identify this project in responses from Todoist until the project gets its
 *              proper ID.
 *
 */
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

/**
 * The task priorities available in Todoist.
 */
enum class Priority(val numeric: Int, val color: Color = Color.NoColor) {
    // TODO why the inversion of numerical values compared to Todoist?

    /**
     * Priority `p1` in Todoist.
     */
    Critical(4, Color(172, 0, 0)),

    /**
     * Priority `p2` in Todoist.
     */
    High(3, Color(248, 128, 28)),

    /**
     * Priority `p3` in Todoist.
     */
    Urgent(2, Color(252, 193, 43)),

    /**
     * Priority `p4` in Todoist.
     */
    Normal(1);

    companion object {
        // From http://stackoverflow.com/a/37795810/539599
        private val map = Priority.values().associateBy(Priority::numeric)

        /**
         * Returns the value corresponding to the given priority level.
         */
        operator fun invoke(numeric: Int) = map[numeric]

        /**
         * Returns the value corresponding to the given name.
         */
        operator fun invoke(name: String) = Priority.valueOf(name)
    }
}

/**
 * A type that can be serialized into and deserialized from JSON.
 *
 * @param Self The type itself. (Workaround for missing meta type `Self`.)
 */
interface JsonRepresentable<Self : JsonRepresentable<Self>> {
    /**
     * *Invariant:* `toJson()` and invoke(json)` are inverse to each other.
     *
     * @return A JSON representation of this instance.
     */
    fun toJson(): String

    /**
     * Interface for the companion object of any implementation of
     * `JsonRepresentable`; note that we require `Self == Outer`.
     *
     * This is a convention, not enforced by the compiler.
     *
     * @param Outer The same type as `Self`.
     */
    interface Companion<Outer : JsonRepresentable<Outer>> {
        /**
         * Constructs a new instance from the given JSON.
         *
         * *Invariant:* `toJson()` and invoke(json)` are inverse to each other.
         *
         * @param json A JSON representation of an `Outer` (`== Self`) value.
         * @return A new instance with property values matching those given in `json`,
         *         if `json` had the right format; `null` otherwise.
         */
        operator fun invoke(json: String): Outer?
    }
}