package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.*
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotView
import com.hristogochev.dotloaders.utils.getColorResource
import com.hristogochev.dotloaders.utils.onAnimationEnd

/**
 * Created by suneet on 10/10/17.
 *
 * Modified by hristogochev on 02/02/23.
 */
class PulseLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultSpacing = 15
    private val defaultDotsColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration: Long = 500
    private val defaultInterpolator = LinearInterpolator()
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
    private lateinit var dotsArray: List<DotView>

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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PulseLoader, 0, 0)

        this.dotRadius =
            typedArray.getDimension(
                R.styleable.PulseLoader_pulse_dotRadius,
                defaultDotRadius
            )
        this.spacing =
            typedArray.getDimensionPixelSize(
                R.styleable.PulseLoader_pulse_spacing,
                defaultSpacing
            )
        this.dotColor = typedArray.getColor(
            R.styleable.PulseLoader_pulse_dotColor,
            defaultDotsColor
        )

        this.animDuration =
            typedArray.getInt(
                R.styleable.PulseLoader_pulse_animDuration,
                defaultAnimDuration.toInt()
            ).toLong()

        this.loaderInterpolator = AnimationUtils.loadInterpolator(
            context,
            typedArray.getResourceId(
                R.styleable.PulseLoader_pulse_interpolator,
                android.R.anim.linear_interpolator
            )
        )

        this.dotCount =
            typedArray.getInt(R.styleable.PulseLoader_pulse_dotCount, defaultDotCount)
        this.animDelay =
            typedArray.getInt(
                R.styleable.PulseLoader_pulse_animDelay,
                defaultAnimDelay.toInt()
            ).toLong()

        typedArray.recycle()
    }

    override fun initViews() {
        setVerticalGravity(Gravity.BOTTOM)

        dotsArray = (0 until dotCount).map {
            DotView(context, dotRadius, dotColor)
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
        for (iCount in 0 until dotCount) {
            val anim = getScaleAnimation(areDotsExpanding, iCount)
            dotsArray[iCount].startAnimation(anim)

            if (iCount == dotCount - 1) {
                anim.onAnimationEnd {
                    if (!animationStopped) playAnimationLoop()
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
