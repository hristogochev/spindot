package com.hristogochev.dotloaders.animation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hristogochev.dotloaders.R

/**
 * Created by hristogochev on 01/02/23.
 */

abstract class AnimationLayout : LinearLayout {

    protected val defaultToggleOnVisibilityChange = true

    protected var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    protected var animationStopped = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    open fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimationLayout, 0, 0)

        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.AnimationLayout_layout_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    abstract fun initViews()

    fun startAnimation() {
        if (!animationStopped) return
        animationStopped = false

        playAnimationLoop()
    }

    fun stopAnimation() {
        if (animationStopped) return
        animationStopped = true

        clearPreviousAnimations()
    }


    protected abstract fun playAnimationLoop()

    protected abstract fun clearPreviousAnimations()


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