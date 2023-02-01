package com.agrawalsuneet.dotsloader.utils

import android.content.ContextWrapper
import android.app.Activity
import android.content.Context


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