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