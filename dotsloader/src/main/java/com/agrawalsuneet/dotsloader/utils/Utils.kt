package com.agrawalsuneet.dotsloader.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View


object Utils {

    fun scanForActivity(context: Context?): Activity? {
        return when (context) {
            null -> null
            is Activity -> context
            is ContextWrapper -> scanForActivity(context.baseContext)
            else -> null
        }

    }
}

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