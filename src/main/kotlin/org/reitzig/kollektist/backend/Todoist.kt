/*
    Copyright (c) Raphael Reitzig 2017

    This file is part of Kollektist.

    Kollektist is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Kollektist is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Kollektist.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.reitzig.kollektist.backend

import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.addProperty
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.typeToken
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.reitzig.kollektist.JsonRepresentable
import org.reitzig.kollektist.Label
import org.reitzig.kollektist.Project
import org.reitzig.kollektist.Task
import java.util.*
import org.reitzig.kollektist.Color as KColor

/**
 * The main backend that interfaces with Todoist following the
 * [official documentation](https://developer.todoist.com/).
 *
 * @param apiToken The token issued for the user. Keep it secret!
 */
class Todoist(var apiToken: String) : Backend {
    /**
     * Where to reach the Todoist API. Note the version!
     */
    val baseUrl = "https://todoist.com/API/v7/sync"

    /**
     * The colors available on Todoist, mapping their color codes to RGB hex.
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


    /**
     * Retrieves a list of resources of the given type from Todoist.
     * Admissible types include `labels` and `projects`.
     */
    private fun <T : JsonRepresentable<T>> getResourceArray(type: String): JsonArray {
        val (request, response, result) = this.baseUrl.httpPost(listOf(
                Pair("token", this.apiToken),
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
    /**
     * Retrieves the current list of labels from the specified Todoist account.
     */
    override fun labels(): List<Label> {
        val jsonArray = getResourceArray<Label>("labels")
        val list = JsonHandler.fromJson<List<Label>>(jsonArray, typeToken<List<Label>>())
        return list.filter { !it.isObsolete }.sortedBy { it.itemOrder }
    }

    /**
     * Retrieves the current list of projects from the specified Todoist account.
     */
    override fun projects(): List<Project> {
        val jsonArray = getResourceArray<Project>("projects")
        val list = JsonHandler.fromJson<List<Project>>(jsonArray, typeToken<List<Project>>())
        return list.filter { !it.isObsolete }.sortedBy { it.itemOrder }
    }

    /**
     * Uploads the specified task to the specified Todoist account.
     */
    override fun add(task: Task) {
        val command = JsonObject()
        command.addProperty("type", "item_add")
        command.addProperty("args", taskApiAdapter.toJsonTree(task))
        command.addProperty("uuid", UUID.randomUUID().toString())
        command.addProperty("temp_id", UUID.randomUUID().toString())

        val (request, response, result) = this.baseUrl.httpPost(listOf(
                Pair("token", this.apiToken),
                Pair("commands", "[${command.toString()}]")
        )).responseString()

        if (response.httpStatusCode == 200) {
            //println(JsonHandler.fromJson<JsonObject>(result.component1()!!, typeToken<JsonObject>()).toString())
            /* Getting back something like:
             { "sync_status": {
                    "fab8a3de-3125-47c8-8790-021ad3b8d286": "ok"
               },
               "temp_id_mapping": {
                    "93c4c929-9d4a-408d-b4e9-c7bcfe73d5d1": 2162409578
               },
               "full_sync": true,
               "sync_token": "Snlp1gLPZWT3TyGbWDUlJEPtoBlrW5syj9eDF6S4r8bs_ftLa6MEwDVQZGp5yrqswMqhXAd42EzNqUXj2-En7JjeAn7-oH9HwQ-Acnk2Tvoo9Nk"
             }
            */
        } else {
            // TODO throws exception
            println("ERROR: ${response.httpStatusCode} response from server")
            println("Request was:\n$request")
            println("Response was:\n$response")
        }
    }
}