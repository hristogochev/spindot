package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CirclesView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.contracts.InitializationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource

/**
 * Created by suneet on 12/29/17.
 * Modifier by hristogochev on 02/02/2023
 */
class RotatingCircularDotsLoader : LinearLayout, InitializationContract, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 30
    private val defaultDotsColor = getColorResource(R.color.loader_selected)
    private val defaultBigCircleRadius = 90
    private val defaultAnimDuration = 5000
    private val defaultToggleOnVisibilityChange = true


    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var dotsColor = defaultDotsColor
    private var bigCircleRadius = defaultBigCircleRadius
    private var animDuration = defaultAnimDuration
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Base view
    private lateinit var circlesView: CirclesView

    // Custom constructors
    constructor(
        context: Context,
        dotsRadius: Int,
        bigCircleRadius: Int,
        dotsColor: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(
        context
    ) {
        this.dotsRadius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.dotsColor = dotsColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initViews()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initViews()
    }


    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RotatingCircularDotsLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            R.styleable.RotatingCircularDotsLoader_rotatingcircular_dotsRadius,
            defaultDotsRadius
        )

        this.dotsColor = typedArray.getColor(
            R.styleable.RotatingCircularDotsLoader_rotatingcircular_dotsColor,
            defaultDotsColor
        )

        this.bigCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.RotatingCircularDotsLoader_rotatingcircular_bigCircleRadius,
            defaultBigCircleRadius
        )

        this.animDuration =
            typedArray.getInt(R.styleable.RotatingCircularDotsLoader_rotatingcircular_animDur, 5000)

        this.isTransitionGroup = typedArray.getBoolean(
            R.styleable.RotatingCircularDotsLoader_rotatingcircular_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    override fun initViews() {
        circlesView = CirclesView(context, dotsRadius, bigCircleRadius, dotsColor)

        addView(circlesView)
    }

    // Animation controls
    override fun startAnimation() {
        // Clear the previous animation
        clearPreviousAnimations()

        // Create new animation
        val rotationAnim = getRotateAnimation()
        circlesView.startAnimation(rotationAnim)
    }

    override fun stopAnimation() {
        // Clear the running animation
        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        circlesView.clearAnimation()
    }

    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
            interpolator = LinearInterpolator()
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = 2 * this.bigCircleRadius + 2 * dotsRadius
        setMeasuredDimension(calWidth, calWidth)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (!toggleOnVisibilityChange) return

        if (visibility != View.VISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }
}