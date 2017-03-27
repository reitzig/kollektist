package org.reitzig.kollektist

/**
 * The color with the given RGB values (integers between 0 and 255).
 */
data class Color(val r: Int, val g: Int, val b: Int) {
    init {
        assert(r in 0..255, {"Invalid value $r"})
        assert(g in 0..255, {"Invalid value $g"})
        assert(b in 0..255, {"Invalid value $b"})
    }

    /**
     * The color corresponding to the given RGB hex string.
     */
    constructor(hex: String) : this(hexExtract(hex, 0), hexExtract(hex, 1), hexExtract(hex, 2))

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

    fun distance(other: Color): Double {
        return Math.sqrt(Math.pow((r - other.r).toDouble(), 2.0) +
                         Math.pow((g - other.g).toDouble(), 2.0) +
                         Math.pow((b - other.b).toDouble(), 2.0))
    }

    companion object {
        /**
         * A dummy color that indicates that no special color should be used.
         */
        val NoColor = Color(-77,-77,-77)

        fun hexExtract(input: String, index: Int): Int {
            assert(Regex("#?[a-fA-F0-6]{6}").matches(input), {"$input is not a hex color"})
            assert(index in 0..2, {"Index must be between 0 and 2."})

            val range = (7 - input.length) + (2*index..2*index+1)
            return input.substring(range).toInt(16)
        }
    }
}
