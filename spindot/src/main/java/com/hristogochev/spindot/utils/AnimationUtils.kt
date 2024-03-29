/**
 * Created by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.utils

import android.view.animation.Animation

fun Animation.onAnimationEnd(onEnd: () -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    })
}