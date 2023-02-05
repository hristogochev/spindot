package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotsView
import com.hristogochev.dotloaders.utils.getColorResource

/**
 * Created by suneet on 12/29/17.
 *
 * Modifier by hristogochev on 02/02/2023
 */
class SpinningLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultDotColor = getColorResource(R.color.loader_selected)
    private val defaultRadius = 90f
    private val defaultAnimDuration: Long = 5000


    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var dotColor = defaultDotColor
    private var radius = defaultRadius
    private var animDuration = defaultAnimDuration

    // Base view
    private lateinit var dotsView: DotsView

    // Animation attributes
    private val linearInterpolator = LinearInterpolator()

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        radius: Float? = null,
        dotColor: Int? = null,
        animDuration: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(
        context
    ) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.dotColor = dotColor ?: defaultDotColor
        this.radius = radius ?: defaultRadius
        this.animDuration = animDuration ?: defaultAnimDuration
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
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
        super.initAttributes(attrs)

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SpinningLoader, 0, 0)

        this.dotRadius = typedArray.getDimension(
            R.styleable.SpinningLoader_spinning_dotRadius,
            defaultDotRadius
        )

        this.dotColor = typedArray.getColor(
            R.styleable.SpinningLoader_spinning_dotColor,
            defaultDotColor
        )

        this.radius = typedArray.getDimension(
            R.styleable.SpinningLoader_spinning_radius,
            defaultRadius
        )

        this.animDuration =
            typedArray.getInt(
                R.styleable.SpinningLoader_spinning_animDuration,
                defaultAnimDuration.toInt()
            ).toLong()

        typedArray.recycle()
    }

    override fun initViews() {
        dotsView = DotsView(context, dotRadius, radius, dotColor)

        addView(dotsView)
    }

    // Animation controls
    override fun playAnimationLoop() {
        val rotationAnim = getRotateAnimation()
        dotsView.startAnimation(rotationAnim)
    }

    override fun clearPreviousAnimations() {
        dotsView.clearAnimation()
    }

    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = animDuration
            fillAfter = true
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
            interpolator = linearInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (2 * radius + 2 * dotRadius).toInt()
        setMeasuredDimension(calWidth, calWidth)
    }
}