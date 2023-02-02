package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd

/**
 * Created by agrawalsuneet on 8/26/18.
 * Modified by hristogochev on 02/02/23.
 */

class ZeeLoader : LinearLayout, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 50
    private val defaultDistanceMultiplier = 4
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration = 500
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var distanceMultiplier = defaultDistanceMultiplier
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var firsDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var animDuration = defaultAnimDuration
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange


    // Views
    private var calWidthHeight: Int = 0
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout

    // Animation attributes
    private var step: Int = 0
    private var animationStopped = false

    // Custom constructors
    constructor(
        context: Context,
        dotsRadius: Int,
        distanceMultiplier: Int,
        firsDotColor: Int,
        secondDotColor: Int,
        toggleOnVisibilityChange: Boolean

    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.distanceMultiplier = distanceMultiplier
        this.firsDotColor = firsDotColor
        this.secondDotColor = secondDotColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initView()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initView()
    }

    // Initialization functions
    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZeeLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            R.styleable.ZeeLoader_zee_dotsRadius,
            defaultDotsRadius
        )
        this.distanceMultiplier = typedArray.getInteger(
            R.styleable.ZeeLoader_zee_distanceMultiplier,
            defaultDistanceMultiplier
        )
        this.firsDotColor = typedArray.getColor(
            R.styleable.ZeeLoader_zee_firstDotsColor,
            defaultFirstDotColor
        )
        this.secondDotColor = typedArray.getColor(
            R.styleable.ZeeLoader_zee_secondDotsColor,
            defaultSecondDotColor
        )
        this.animDuration =
            typedArray.getInt(R.styleable.ZeeLoader_zee_animDuration, defaultAnimDuration)
        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.ZeeLoader_zee_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initView() {
        this.gravity = Gravity.CENTER_HORIZONTAL

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        if (calWidthHeight == 0) calWidthHeight = (2 * dotsRadius * distanceMultiplier)

        firstCircle = CircleView(context, dotsRadius, firsDotColor)
        val firstParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }

        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        val secondParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        }

        relativeLayout.addView(firstCircle, firstParam)
        relativeLayout.addView(secondCircle, secondParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        addView(relativeLayout, relParam)
    }

    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        clearPreviousAnimations()

        val firstCircleAnim = getTranslateAnim(1)

        firstCircle.startAnimation(firstCircleAnim)

        val secondCircleAnim = getTranslateAnim(2)

        secondCircleAnim.onAnimationEnd {
            step++
            if (step > 3) {
                step = 0
            }
            if (!animationStopped) startAnimation()
        }

        secondCircle.startAnimation(secondCircleAnim)
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        firstCircle.clearAnimation()
        secondCircle.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {
        val circleDiameter = 2 * dotsRadius
        val finalDistance = ((distanceMultiplier - 1) * circleDiameter).toFloat()

        var fromXPos = 0.0f
        var fromYPos = 0.0f

        var toXPos = 0.0f
        var toYPos = 0.0f

        when (step) {
            0 -> {
                toXPos = if (circleCount == 1) finalDistance else -1 * finalDistance
            }
            1 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    toYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    toYPos = -1 * finalDistance
                }
            }
            2 -> {
                if (circleCount == 1) {
                    toXPos = finalDistance
                    fromYPos = finalDistance
                    toYPos = fromYPos
                } else {
                    toXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                    toYPos = fromYPos
                }
            }
            3 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    fromYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                }
            }
        }

        return TranslateAnimation(
            fromXPos, toXPos,
            fromYPos, toYPos
        ).apply {
            duration = animDuration.toLong()
            fillAfter = true
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = 0
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * dotsRadius * distanceMultiplier)
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