package com.hristogochev.spinkit.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.*
import android.widget.RelativeLayout
import com.hristogochev.spinkit.R
import com.hristogochev.spinkit.animation.AnimationLayout
import com.hristogochev.spinkit.basicviews.DotView
import com.hristogochev.spinkit.utils.getColorResource
import com.hristogochev.spinkit.utils.onAnimationEnd

/**
 * Modified by hristogochev on 02/02/23.
 */

class TrailingLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 50f
    private val defaultRadius = 200f
    private val defaultDotColor = getColorResource(R.color.loader_selected)
    private val defaultDotTrailCount = 6
    private val defaultAnimDuration: Long = 2000
    private val defaultAnimDelayDivider = 10

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var radius = defaultRadius
    private var dotColor = defaultDotColor
    private var dotTrailCount = defaultDotTrailCount
    private var animDuration = defaultAnimDuration
    private var animDelay = animDuration / defaultAnimDelayDivider

    // Views
    private lateinit var mainDot: DotView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var trailingDots: List<DotView>

    // Animation attributes
    private var calWidthHeight: Int = 0
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        radius: Float? = null,
        dotColor: Int? = null,
        dotTrailCount: Int? = null,
        animDuration: Long? = null,
        animDelay: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.radius = radius ?: defaultRadius
        this.dotColor = dotColor ?: defaultDotColor
        this.dotTrailCount = dotTrailCount ?: defaultDotTrailCount
        this.animDuration = animDuration ?: defaultAnimDuration
        this.animDelay = animDelay ?: (this.animDuration / defaultAnimDelayDivider)
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initViews()
    }

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
            context.obtainStyledAttributes(attrs, R.styleable.TrailingLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius = getDimension(
                    R.styleable.TrailingLoader_trailing_dotRadius,
                    defaultDotRadius
                )
                radius = getDimension(
                    R.styleable.TrailingLoader_trailing_radius,
                    defaultRadius
                )
                dotColor = getColor(
                    R.styleable.TrailingLoader_trailing_dotColor,
                    defaultDotColor
                )
                dotTrailCount = getInt(
                    R.styleable.TrailingLoader_trailing_dotTrailCount,
                    defaultDotTrailCount
                )
                animDuration = getInt(
                    R.styleable.TrailingLoader_trailing_animDuration,
                    defaultAnimDuration.toInt()
                ).toLong()
                animDelay = getInt(
                    R.styleable.TrailingLoader_trailing_animDelay,
                    (animDuration / defaultAnimDelayDivider).toInt()
                ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        this.gravity = Gravity.CENTER_HORIZONTAL

        if (calWidthHeight == 0) {
            calWidthHeight = ((2 * radius) + (2 * dotRadius)).toInt()
        }

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        mainDot = DotView(context, dotRadius, dotColor)

        trailingDots = (0 until dotTrailCount).map {
            DotView(context, dotRadius, dotColor)
        }

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        relativeLayout.addView(mainDot)
        trailingDots.forEach {
            relativeLayout.addView(it)
        }

        this.addView(relativeLayout, relParam)
    }


    // Animation controls
    override fun playAnimationLoop() {
        if (animationStopped) return

        val mainCircleAnim = getRotateAnimation()
        mainDot.startAnimation(mainCircleAnim)

        for (i in 1..dotTrailCount) {
            val animSet = getTrailingAnimation(i, ((animDuration * (2 + i)) / 20))
            trailingDots[i - 1].startAnimation(animSet)

            if (i == dotTrailCount - 1) {
                animSet.onAnimationEnd {
                    Handler(Looper.getMainLooper()).postDelayed({
                        playAnimationLoop()
                    }, animDelay)
                }
            }
        }
    }

    override fun clearPreviousAnimations() {
        mainDot.clearAnimation()
        for (i in 1..dotTrailCount) {
            trailingDots[i - 1].clearAnimation()
        }
    }

    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        ).apply {
            duration = animDuration
            fillAfter = true
            interpolator = accelerateDecelerateInterpolator
            startOffset = (animDuration / 10)
        }
    }

    private fun getTrailingAnimation(count: Int, delay: Long): AnimationSet {
        val animSet = AnimationSet(true)

        val scaleFactor: Float = 1.00f - (count.toFloat() / 20)

        val scaleAnim = ScaleAnimation(
            scaleFactor, scaleFactor, scaleFactor, scaleFactor,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        animSet.addAnimation(scaleAnim)


        val rotateAnim = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        ).apply {
            duration = animDuration
        }

        animSet.addAnimation(rotateAnim)
        animSet.duration = animDuration
        animSet.fillAfter = false
        animSet.interpolator = accelerateDecelerateInterpolator
        animSet.startOffset = delay

        return animSet
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = ((2 * radius) + (2 * dotRadius)).toInt()
        }
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }
}