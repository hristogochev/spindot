/**
 * Created by suneet on 10/10/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.*
import com.hristogochev.spindot.R
import com.hristogochev.spindot.animation.AnimationLayout
import com.hristogochev.spindot.basicviews.DotView
import com.hristogochev.spindot.utils.getColorResource
import com.hristogochev.spindot.utils.onAnimationEnd

class PulsingLoader : com.hristogochev.spindot.animation.AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultSpacing = 15
    private val defaultDotsColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration: Long = 500
    private val defaultInterpolator = AccelerateInterpolator()
    private val defaultDotCount = 8
    private val defaultAnimDelay: Long = 100

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var spacing = defaultSpacing
    private var dotColor = defaultDotsColor
    private var animDuration = defaultAnimDuration
    private var loaderInterpolator: Interpolator = defaultInterpolator
    private var dotCount = defaultDotCount
    private var animDelay = defaultAnimDelay

    // Animation attributes
    private var areDotsExpanding = true

    // Views
    private lateinit var dotsArray: List<com.hristogochev.spindot.basicviews.DotView>

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        spacing: Int? = null,
        dotColor: Int? = null,
        animDuration: Long? = null,
        interpolator: Interpolator? = null,
        dotCount: Int? = null,
        animDelay: Long? = null,
        toggleOnVisibilityChange: Boolean? = null,
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.spacing = spacing ?: defaultSpacing
        this.dotColor = dotColor ?: defaultDotsColor
        this.animDuration = animDuration ?: defaultAnimDuration
        this.loaderInterpolator = interpolator ?: defaultInterpolator
        this.dotCount = dotCount ?: defaultDotCount
        this.animDelay = animDelay ?: defaultAnimDelay
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PulsingLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius =
                    getDimension(
                        R.styleable.PulsingLoader_pulsing_dotRadius,
                        defaultDotRadius
                    )
                spacing =
                    getDimensionPixelSize(
                        R.styleable.PulsingLoader_pulsing_spacing,
                        defaultSpacing
                    )
                dotColor = getColor(
                    R.styleable.PulsingLoader_pulsing_dotColor,
                    defaultDotsColor
                )

                animDuration =
                    getInt(
                        R.styleable.PulsingLoader_pulsing_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()

                loaderInterpolator = AnimationUtils.loadInterpolator(
                    context,
                    getResourceId(
                        R.styleable.PulsingLoader_pulsing_interpolator,
                        android.R.anim.linear_interpolator
                    )
                )

                dotCount =
                    getInt(R.styleable.PulsingLoader_pulsing_dotCount, defaultDotCount)
                animDelay =
                    getInt(
                        R.styleable.PulsingLoader_pulsing_animDelay,
                        defaultAnimDelay.toInt()
                    ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        setVerticalGravity(Gravity.BOTTOM)

        dotsArray = (0 until dotCount).map {
            com.hristogochev.spindot.basicviews.DotView(context, dotRadius, dotColor)
        }

        val size = (2 * dotRadius).toInt()

        dotsArray.forEachIndexed { iCount, circle ->
            val params = LayoutParams(size, size)
            if (iCount != dotCount - 1) {
                params.rightMargin = spacing
            }
            addView(circle, params)
        }
    }


    // Animation controls
    override fun playAnimationLoop() {
        if (animationStopped) return

        for (iCount in 0 until dotCount) {
            val anim = getScaleAnimation(areDotsExpanding, iCount)
            dotsArray[iCount].startAnimation(anim)

            if (iCount == dotCount - 1) {
                anim.onAnimationEnd {
                    playAnimationLoop()
                }
            } else {
                anim.onAnimationEnd {
                    dotsArray[iCount].visibility =
                        if (!areDotsExpanding) View.VISIBLE else View.INVISIBLE
                }
            }
        }
        areDotsExpanding = !areDotsExpanding
    }

    override fun clearPreviousAnimations() {
        dotsArray.forEach {
            it.clearAnimation()
        }
    }

    // Animations
    private fun getScaleAnimation(isExpanding: Boolean, delay: Int): AnimationSet {
        val scaleAnim: ScaleAnimation = if (isExpanding) {
            ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        } else {
            ScaleAnimation(
                1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        }.apply {
            duration = animDuration
            fillAfter = true
            repeatCount = 0
            startOffset = (animDelay * delay)
        }

        return AnimationSet(true).apply {
            addAnimation(scaleAnim)
            interpolator = loaderInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calHeight = (2 * dotRadius).toInt()
        val calWidth = (((2 * dotCount * dotRadius) + ((dotCount - 1) * spacing))).toInt()

        setMeasuredDimension(calWidth, calHeight)
    }
}
