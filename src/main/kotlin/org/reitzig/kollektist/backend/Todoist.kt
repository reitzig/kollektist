package org.reitzig.kollektist.backend

import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.typeToken
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.reitzig.kollektist.JsonRepresentable
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import org.reitzig.kollektist.Color as KColor

/**
 * Refer to https://developer.todoist.com/ for documentation.
 * TODO document
 * @author Raphael Reitzig
 */
object Todoist : Backend {
    /**
     * Where to reach the Todoist API.
     */
    val baseUrl = "https://todoist.com/API/v7/sync"

    /**
     * The colors available on Todoist, mapping their color code to RGB hex.
     */
    object Colors {
        private val colors = mapOf(
                null to KColor.NoColor,
                0 to KColor("#95ef63"),
                1 to KColor("#ff8581"),
                2 to KColor("#ffc471"),
                3 to KColor("#f9ec75"),
                4 to KColor("#a8c8e4"),
                5 to KColor("#d2b8a3"),
                6 to KColor("#e2a8e4"),
                7 to KColor("#cccccc"),
                8 to KColor("#fb886e"),
                9 to KColor("#ffcc00"),
                10 to KColor("#74e8d3"),
                11 to KColor("#3bd5fb"),
                12 to KColor("#dc4fad"),
                13 to KColor("#ac193d"),
                14 to KColor("#d24726"),
                15 to KColor("#82ba00"),
                16 to KColor("#03b3b2"),
                17 to KColor("#008299"),
                18 to KColor("#5db2ff"),
                19 to KColor("#0072c6"),
                20 to KColor("#000000"),
                22 to KColor("#777777")
        )

        operator fun get(code: Int?): KColor {
            return colors.getOrDefault(code, KColor.NoColor)
        }
    }


    private fun <T : JsonRepresentable<T>> getResourceArray(type: String): JsonArray {
        val (request, response, result) = "https://todoist.com/API/v7/sync".httpPost(listOf(
                Pair("sync_token", "*"), // TODO make incremental?
                Pair("resource_types", "[\"$type\"]")
        )).responseString()

        if (response.httpStatusCode == 200) {
            return JsonHandler.fromJson<JsonObject>(result.component1()!!, typeToken<JsonObject>())[type]
                    as JsonArray
        } else {
            println("ERROR: ${response.httpStatusCode} response from server")
            println("Request was:\n$request")
            println("Response was:\n$response")
            return jsonArray()
        }
    }

    // TODO get once async, store future in (lazy?) val?
    override fun labels(): List<Label> {
        val jsonArray = getResourceArray<Label>("labels")
        val list = JsonHandler.fromJson<List<Label>>(jsonArray, typeToken<List<Label>>())
        return list.filter { !it.isObsolete }.sortedBy { it.itemOrder }
    }

    override fun projects(): List<Project> {
        val jsonArray = getResourceArray<Project>("projects")
        val list = JsonHandler.fromJson<List<Project>>(jsonArray, typeToken<List<Project>>())
        return list.filter { !it.isObsolete }.sortedBy { it.itemOrder }
    }

    /**
     * Uploads the specified task to the connected Todoist account.
     */
    override fun add(task: Task) {
        println("Stored task $task")
    }
}