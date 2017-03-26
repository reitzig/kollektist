package org.reitzig.kollektist.backend

import com.github.kittinunf.fuel.httpPost
import org.reitzig.kollektist.Color
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

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
    val colors = mapOf(
            0 to Color("#95ef63"),
            1 to Color("#ff8581"),
            2 to Color("#ffc471"),
            3 to Color("#f9ec75"),
            4 to Color("#a8c8e4"),
            5 to Color("#d2b8a3"),
            6 to Color("#e2a8e4"),
            7 to Color("#cccccc"),
            8 to Color("#fb886e"),
            9 to Color("#ffcc00"),
            10 to Color("#74e8d3"),
            11 to Color("#3bd5fb"),
            12 to Color("#dc4fad"),
            13 to Color("#ac193d"),
            14 to Color("#d24726"),
            15 to Color("#82ba00"),
            16 to Color("#03b3b2"),
            17 to Color("#008299"),
            18 to Color("#5db2ff"),
            19 to Color("#0072c6"),
            20 to Color("#000000"),
            22 to Color("#777777")
    )


    private fun getResourceList(type: String): List<String> {
        "https://todoist.com/API/v7/sync".httpPost(listOf(
                Pair("sync_token", "*"), // TODO make incremental?
                Pair("resource_types", "[\"$type\"]")
        )).responseString { request, response, result ->
            println(response)
            println(result)
        }
        // TODO wait!
        return listOf("Test")
    }

    // TODO get once async, store future in (lazy?) val?
    override fun labels(): Set<Label> {
        return getResourceList("labels").map { Label(it) }.toSet()
    }

    override fun projects(): Set<Project> {
        return getResourceList("projects").map { Project(it) }.toSet()
    }

    /**
     * Uploads the specified task to the connected Todoist account.
     */
    override fun add(task: Task) {
        println("Stored task $task")
    }


}