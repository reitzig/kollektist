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

package org.reitzig.kollektist

/**
 * The color with the given RGB values (integers between 0 and 255).
 */
data class Color(val r: Int, val g: Int, val b: Int) {
    val hex: String

    init {
        assert(r in 0..255, {"Invalid value $r"})
        assert(g in 0..255, {"Invalid value $g"})
        assert(b in 0..255, {"Invalid value $b"})
        this.hex = "#${r.toString(16)}${g.toString(16)}${b.toString(16)}"
    }

    /**
     * The color with RGB values derived from the given fractions of 255, rounded to the nearest integer.
     */
    constructor(r: Double, g: Double, b: Double): this(Math.round(r * 255).toInt(),
                                                       Math.round(g * 255).toInt(),
                                                       Math.round(b * 255).toInt()) {
        assert(r in 0.0..1.0, {"Invalid value $r"})
        assert(g in 0.0..1.0, {"Invalid value $g"})
        assert(b in 0.0..1.0, {"Invalid value $b"})
    }

    /**
     * Computes the distance between this and the given color.
     */
    fun distance(other: Color): Double {
        // TODO this is no good. --> http://stackoverflow.com/a/4356523/539599
        return Math.sqrt(Math.pow((r - other.r).toDouble(), 2.0) +
                         Math.pow((g - other.g).toDouble(), 2.0) +
                         Math.pow((b - other.b).toDouble(), 2.0))
    }

    companion object {
        /**
         * A dummy color that indicates that no special color should be used.
         */
        val NoColor = Color(-77,-77,-77)
        // TODO why does this not trigger the assertion? Or does it?

        /**
         * Creates a new color from the given hex string.
         *
         * @param hex A color string as e.g. seen in HTML, i.e. `#FFFFFF` is white.
         * @return The color obtained when reading the hex number as three one-byte numbers (RGB).
         */
        operator fun invoke(hex: String): Color {
            assert(Regex("#?[a-fA-F0-6]{6}").matches(hex), { "$hex is not a hex color" })
            val r = hex.substring((hex.length - 6) + (0..1)).toInt(16)
            val g = hex.substring((hex.length - 6) + (2..3)).toInt(16)
            val b = hex.substring((hex.length - 6) + (4..5)).toInt(16)
            return Color(r, g, b)
        }
    }
}
