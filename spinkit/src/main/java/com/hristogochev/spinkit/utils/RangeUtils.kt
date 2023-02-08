package com.hristogochev.spinkit.utils

import java.util.*


/**
 * Created by suneet on 17/7/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

fun ClosedFloatingPointRange<Float>.random() =
    (Random().nextFloat() * (endInclusive - start)) + start

fun ClosedRange<Int>.random() =
    Random().nextInt((endInclusive + 1) - start) + start
