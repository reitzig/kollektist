package org.reitzig.kollektist.backend

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.typeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Priority
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

val labelAdapter = typeAdapter<Label> {
    write {
        beginObject()
        name("name"); value(it.name)
        name("color"); value(it.color)
        name("id"); value(it.id)
        name("item_order"); value(it.itemOrder)
        endObject()
    }

    read {
        var name: String = ""
        var color: Int? = null
        var id: Int = -1
        var itemOrder: Int? = null
        var isDeleted: Boolean = false

        beginObject()
        while (this.hasNext()) {
            when (this.nextName()) {
                "name"       -> name = this.nextString()
                "color"      -> color = this.nextInt()
                "id"         -> id = this.nextInt()
                "item_order" -> itemOrder = this.nextInt()
                "is_deleted" -> isDeleted = this.nextInt() > 0
            }
        }
        endObject()

        Label(name, id, color, itemOrder, isDeleted)
    }
}

val projectAdapter = typeAdapter<Project> {

    write {
        beginObject()
        name("name"); value(it.name)
        name("color"); value(it.color)
        name("parent_id"); value(it.parent)
        name("id"); value(it.id)
        name("indent"); value(it.indent)
        name("item_order"); value(it.itemOrder)
        endObject()
    }

    read {
        var name: String = ""
        var color: Int? = null
        var parentId: Int? = null
        var id: Int = -1
        var itemOrder: Int? = null
        var indent: Int? = null
        var isObsolete: Boolean = false

        beginObject()
        while (this.hasNext()) {
            when (this.nextName()) {
                "name"        -> name = this.nextString()
                "color"       -> color = this.nextInt()
                "parent_id"   -> parentId = this.nextIntOrNull()
                "id"          -> id = this.nextInt()
                "indent"      -> indent = this.nextInt()
                "item_order"  -> itemOrder = this.nextInt()
                "is_deleted"  -> isObsolete = isObsolete or (this.nextInt() > 0)
                "is_archived" -> isObsolete = isObsolete or (this.nextInt() > 0)
                else          -> this.skipValue()
            }
        }
        endObject()

        Project(name, id, color, itemOrder, parentId, indent, isObsolete)
    }

}

val taskAdapter = typeAdapter<Task> {
    write {
        beginObject()

        name("content"); value(it.description)
        it.project?.let {
            name("project")
            projectAdapter.write(this, it)
        }
        name("priority"); value(it.priority.numeric)
        name("labels")
        beginArray()
        it.labels.forEach {
            labelAdapter.write(this, it)
        }
        endArray()
        // TODO due date?

        endObject()
    }

    read {
        var description: String = ""
        var project: Project? = null
        var priority: Priority = Priority.Normal
        val labels: MutableSet<Label> = mutableSetOf()

        beginObject()
        while (this.hasNext()) {
            when (this.nextName()) {
                "content"  -> description = this.nextString()
                "project"  -> project = projectAdapter.read(this)
                "priority" -> priority = Priority(this.nextInt())!!
                "labels"   -> {
                    beginArray()
                    while (this.hasNext() && this.peek() == JsonToken.BEGIN_OBJECT) {
                        labels.add(labelAdapter.read(this))
                    }
                    endArray()
                }
            }
        }
        endObject()

        Task(description, project, labels, priority)
    }
}

val taskApiAdapter = typeAdapter<Task> {
    write {
        beginObject()

        name("content"); value(it.description)
        it.project?.let {
            name("project_id"); value(it.id)
        }
        name("priority"); value(it.priority.numeric)
        name("labels")
        beginArray()
        it.labels.forEach {
            value(it.id)
        }
        endArray()

        endObject()
    }

    read {
        throw NotImplementedError()
    }
}

val JsonHandler = GsonBuilder()
        .registerTypeAdapter<Label>(labelAdapter)
        .registerTypeAdapter<Project>(projectAdapter)
        .registerTypeAdapter<Task>(taskAdapter)
        .create()!!

// Extensions to JsonReader for reading nullable values
// Pending: https://github.com/SalomonBrys/Kotson/issues/26

/**
 * Returns the {@link com.google.gson.stream.JsonToken#NUMBER int} value of the next token,
 * consuming it. If the next token is {@code NULL}, this method returns {@code null}.
 * If the next token is a string, it will attempt to parse it as an int.
 * If the next token's numeric value cannot be exactly represented by a Java {@code int},
 * this method throws.
 *
 * @throws IllegalStateException if the next token is not a literal value.
 * @throws NumberFormatException if the next literal value is not null but
 *      cannot be parsed as a number, or exactly represented as an int.
 */
fun JsonReader.nextIntOrNull(): Int? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextInt()
    } else {
        this.nextNull()
        return null
    }
}

/**
 * Returns the {@link com.google.gson.stream.JsonToken#BOOLEAN boolean} value of the next token,
 * consuming it. If the next token is {@code NULL}, this method returns {@code null}.
 *
 * @throws IllegalStateException if the next token is not a boolean or if
 *     this reader is closed.
 */
fun JsonReader.nextBooleanOrNull(): Boolean? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextBoolean()
    } else {
        this.nextNull()
        return null
    }
}

/**
 * Returns the {@link com.google.gson.stream.JsonToken#NUMBER double} value of the next token,
 * consuming it. If the next token is {@code NULL}, this method returns {@code null}.
 * If the next token is a string, it will attempt to parse it as a double using {@link Double#parseDouble(String)}.
 *
 * @throws IllegalStateException if the next token is not a literal value.
 * @throws NumberFormatException if the next literal value cannot be parsed
 *     as a double, or is non-finite.
 */
fun JsonReader.nextDoubleOrNull(): Double? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextDouble()
    } else {
        this.nextNull()
        return null
    }
}

/**
 * Returns the {@link com.google.gson.stream.JsonToken#NUMBER long} value of the next token,
 * consuming it. If the next token is {@code NULL}, this method returns {@code null}.
 * If the next token is a string, this method will attempt to parse it as a long.
 * If the next token's numeric value cannot be exactly represented by a Java {@code long}, this method throws.
 *
 * @throws IllegalStateException if the next token is not a literal value.
 * @throws NumberFormatException if the next literal value cannot be parsed
 *     as a number, or exactly represented as a long.
 */
fun JsonReader.nextLongOrNull(): Long? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextLong()
    } else {
        this.nextNull()
        return null
    }
}

/**
 * Returns the {@link com.google.gson.stream.JsonToken#STRING string} value of the next token,
 * consuming it. If the next token is {@code NULL}, this method returns {@code null}.
 * If the next token is a number, it will return its string form.
 *
 * @throws IllegalStateException if the next token is not a string or if
 *     this reader is closed.
 */
fun JsonReader.nextStringOrNull(): String? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextString()
    } else {
        this.nextNull()
        return null
    }
}