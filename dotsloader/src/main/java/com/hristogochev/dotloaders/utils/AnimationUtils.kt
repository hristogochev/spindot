package com.hristogochev.dotloaders.utils

import android.view.animation.Animation

/**
 * Created by hristogochev on 02/02/23.
 */
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