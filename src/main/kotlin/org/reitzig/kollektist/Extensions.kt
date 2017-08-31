package org.reitzig.kollektist

/**
 * Shifts the given range by this integer.
 *
 * *Example:* `1 + (0..1) == (1..2)`
 */
operator fun Int.plus(rhs: IntRange): IntRange {
    return rhs.shifted(by = this)
}

/**
 * Shifts this range by the given integer.
 *
 * *Example:* `(0..1) + 1 == (1..2)`
 */
operator fun IntRange.plus(rhs: Int): IntRange {
    return this.shifted(by = rhs)
}

/**
 * Shifts this range by the given integer.
 *
 * *Example:* `(0..1) + 1 == (1..2)`
 */
fun IntRange.shifted(by: Int): IntRange {
    return (this.first + by)..(this.last + by)
}