/**
 *  Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View

fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Suppress("DEPRECATION")
fun View.getColorResource(id: Int): Int {
    return if (Build.VERSION.SDK_INT >= 23) {
        resources.getColor(id, context.theme)
    } else {
        resources.getColor(id)
    }
}