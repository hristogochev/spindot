package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd

class TrailingCircularDotsLoader : LinearLayout, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 50
    private val defaultBigCircleRadius = 200
    private val defaultCircleColor = getColorResource(R.color.loader_selected)
    private val defaultNoOfTrailingDots = 6
    private val defaultAnimDuration = 2000
    private val defaultAnimDelayDivider = 10
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var bigCircleRadius = defaultBigCircleRadius
    private var circleColor = defaultCircleColor
    private var noOfTrailingDots = defaultNoOfTrailingDots
    private var animDuration = defaultAnimDuration
    private var animDelay = animDuration / defaultAnimDelayDivider
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Views
    private lateinit var mainCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var trailingCirclesArray: List<CircleView>

    // Animation attributes
    private var calWidthHeight: Int = 0
    private var animationStopped = false

    // Custom constructors
    constructor(
        context: Context?,
        dotsRadius: Int,
        bigCircleRadius: Int,
        circleColor: Int,
        noOfTrailingDots: Int,
        animDuration: Int,
        animDelay: Int = animDuration / 10,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.circleColor = circleColor
        this.noOfTrailingDots = noOfTrailingDots
        this.animDuration = animDuration
        this.animDelay = animDelay
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
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
    private fun initAttributes(attrs: AttributeSet) {

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TrailingCircularDotsLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_dotsRadius,
            defaultDotsRadius
        )
        this.bigCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_bigCircleRadius,
            defaultBigCircleRadius
        )
        this.circleColor = typedArray.getColor(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_dotsColor,
            defaultCircleColor
        )
        this.noOfTrailingDots = typedArray.getInt(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_noOfTrailingDots,
            defaultNoOfTrailingDots
        )
        this.animDuration = typedArray.getInt(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_animDuration,
            defaultAnimDuration
        )
        this.animDelay = typedArray.getInt(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_animDelay,
            animDuration / defaultAnimDelayDivider
        )
        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.TrailingCircularDotsLoader_trailingcircular_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initViews() {
        this.gravity = Gravity.CENTER_HORIZONTAL

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * bigCircleRadius) + (2 * dotsRadius)
        }

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        mainCircle = CircleView(context, dotsRadius, circleColor)

        trailingCirclesArray = (0 until noOfTrailingDots).map {
            CircleView(context, dotsRadius, circleColor)
        }

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        relativeLayout.addView(mainCircle)
        trailingCirclesArray.forEach {
            relativeLayout.addView(it)
        }

        this.addView(relativeLayout, relParam)
    }


    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        val mainCircleAnim = getRotateAnimation()
        mainCircle.startAnimation(mainCircleAnim)

        for (i in 1..noOfTrailingDots) {
            val animSet = getTrailingAnimation(i, ((animDuration * (2 + i)) / 20))
            trailingCirclesArray[i - 1].startAnimation(animSet)

            if (i == noOfTrailingDots - 1) {
                animSet.onAnimationEnd {
                    if (!animationStopped) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startAnimation()
                        }, animDelay.toLong())
                    }
                }
            }
        }
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        mainCircle.clearAnimation()
        for (i in 1..noOfTrailingDots) {
            trailingCirclesArray[i - 1].clearAnimation()
        }
    }


    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        ).apply {
            duration = animDuration.toLong()
            fillAfter = true
            interpolator = AccelerateDecelerateInterpolator()
            startOffset = (animDuration / 10).toLong()
        }
    }

    private fun getTrailingAnimation(count: Int, delay: Int): AnimationSet {
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
            duration = animDuration.toLong()
        }

        animSet.addAnimation(rotateAnim)
        animSet.duration = animDuration.toLong()
        animSet.fillAfter = false
        animSet.interpolator = AccelerateDecelerateInterpolator()
        animSet.startOffset = delay.toLong()

        return animSet
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * bigCircleRadius) + (2 * dotsRadius)
        }
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

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