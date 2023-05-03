/**
 * Created by suneet on 17/7/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.utils

import java.util.*

fun ClosedFloatingPointRange<Float>.random() =
    (Random().nextFloat() * (endInclusive - start)) + start

fun ClosedRange<Int>.random() =
    Random().nextInt((endInclusive + 1) - start) + start
