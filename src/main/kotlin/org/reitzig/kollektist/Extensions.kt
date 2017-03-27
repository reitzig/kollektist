package org.reitzig.kollektist

operator fun Int.plus(rhs: IntRange): IntRange {
    return rhs.shifted(by = this)
}

operator fun IntRange.plus(rhs: Int): IntRange {
    return this.shifted(by = rhs)
}

fun IntRange.shifted(by: Int): IntRange {
    return (this.first + by)..(this.last + by)
}