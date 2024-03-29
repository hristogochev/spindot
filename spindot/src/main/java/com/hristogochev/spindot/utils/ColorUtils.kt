/**
 * Created by suneet on 17/7/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.utils

import android.graphics.Color
import kotlin.math.roundToInt

fun adjustAlpha(color: Int, factor: Float): Int {
    val alpha = (Color.alpha(color) * factor).roundToInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}


