package org.reitzig.kollektist.backend

import com.github.kittinunf.fuel.httpPost
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task

/**
 * Refer to https://developer.todoist.com/ for documentation.
 * TODO document
 * @author Raphael Reitzig
 */
object Todoist: Backend {
    /**
     * Where to reach the Todoist API.
     */
    val baseUrl = "https://todoist.com/API/v7/sync"

    /**
     * The colors available on Todoist, mapping their color code to RGB hex.
     */
    val colors = mapOf(
         0 to "#95ef63",
         1 to "#ff8581",
         2 to "#ffc471",
         3 to "#f9ec75",
         4 to "#a8c8e4",
         5 to "#d2b8a3",
         6 to "#e2a8e4",
         7 to "#cccccc",
         8 to "#fb886e",
         9 to "#ffcc00",
        10 to "#74e8d3",
        11 to "#3bd5fb",
        12 to "#dc4fad",
        13 to "#ac193d",
        14 to "#d24726",
        15 to "#82ba00",
        16 to "#03b3b2",
        17 to "#008299",
        18 to "#5db2ff",
        19 to "#0072c6",
        20 to "#000000",
        22 to "#777777"
    )


    private fun getResourceList(type: String): List<String> {
        "https://todoist.com/API/v7/sync".httpPost(listOf(
                Pair("sync_token", "*"), // TODO make incremental?
                Pair("resource_types", "[\"$type\"]")
        )).responseString { request, response, result ->
            println(request)
            println(response)
            println(result)
        }
        // TODO wait!
        return listOf("Test")
    }

    // TODO get once async, store future in (lazy?) val?
    override fun labels(): Set<Label> {
        return getResourceList("labels").map(::Label).toSet()
    }

    override fun projects(): Set<Project> {
        return getResourceList("projects").map(::Project).toSet()
    }

    /**
     * Uploads the specified task to the connected Todoist account.
     */
    override fun add(task: Task) {
        println("Stored task $task")
    }


}