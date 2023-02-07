package com.hristogochev.dotloaders.animation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.hristogochev.dotloaders.R
import java.util.*

/**
 * Created by hristogochev on 01/02/23.
 */

abstract class AnimationView : View {
    protected val defaultToggleOnVisibilityChange = true

    protected var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    protected abstract var animDuration: Long

    private var animationTimer: Timer? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    open fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimationView, 0, 0)

        try {
            with(typedArray) {
                toggleOnVisibilityChange = getBoolean(
                    R.styleable.AnimationView_view_toggleOnVisibilityChange,
                    defaultToggleOnVisibilityChange
                )
            }
        } finally {
            typedArray.recycle()
        }
    }

    fun startAnimation() {
        if (animationTimer != null) return

        animationTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    playAnimationLoop()
                }
            }, 0, animDuration)
        }
    }

    fun stopAnimation() {
        if (animationTimer == null) return

        animationTimer?.cancel()
        animationTimer = null
    }

    protected abstract fun playAnimationLoop()

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (!toggleOnVisibilityChange) return

        if (visibility != VISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }
}