package org.reitzig.kollektist.backend

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.typeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project

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

fun JsonReader.nextIntOrNull(): Int? {
    if (this.peek() != JsonToken.NULL) {
        return this.nextInt()
    } else {
        this.nextNull()
        return null
    }
}

val JsonHandler = GsonBuilder()
        .registerTypeAdapter<List<Label>> {
            write {
                beginArray()
                it.forEach { element ->
                    labelAdapter.write(this, element)
                }
                endArray()
            }

            read {
                val result: MutableList<Label> = mutableListOf()
                beginArray()
                while (this.hasNext()) {
                    result.add(labelAdapter.read(this))
                }
                endArray()
                result
            }
        }
        .registerTypeAdapter<List<Project>> {
            write {
                beginArray()
                it.forEach { element ->
                    projectAdapter.write(this, element)
                }
                endArray()
            }

            read {
                val result: MutableList<Project> = mutableListOf()
                beginArray()
                while (this.hasNext()) {
                    result.add(projectAdapter.read(this))
                }
                endArray()
                result
            }
        }
        .registerTypeAdapter<Label>(labelAdapter)
        .registerTypeAdapter<Project>(projectAdapter)
        .create()