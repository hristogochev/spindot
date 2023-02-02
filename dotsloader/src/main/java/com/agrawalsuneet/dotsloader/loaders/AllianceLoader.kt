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
 * Created by agrawalsuneet on 9/1/18.
 * Modifier by hristogochev on 02/02/23.
 */

class AllianceLoader : LinearLayout, AnimationContract {
    // Default input attributes
    private val defaultDotsRadius = 50
    private val defaultDistanceMultiplier = 4
    private val defaultFirstColor = getColorResource(R.color.loader_selected)
    private val defaultSecondColor = getColorResource(R.color.loader_selected)
    private val defaultThirdColor = getColorResource(R.color.loader_selected)
    private val defaultDrawOnlyStroke = false
    private val defaultStrokeWidth = 20
    private val defaultAnimDuration = 500
    private val defaultToggleOnVisibilityChange = true


    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var distanceMultiplier = defaultDistanceMultiplier
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var firsDotColor = defaultFirstColor
    private var secondDotColor = defaultSecondColor
    private var thirdDotColor = defaultThirdColor
    private var drawOnlyStroke = defaultDrawOnlyStroke
    private var strokeWidth = defaultStrokeWidth
    private var animDuration = defaultAnimDuration
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Views
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var thirdCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout

    // Animation attributes
    private var step: Int = 0
    private var calWidthHeight: Int = 0
    private var posArrayList: ArrayList<ArrayList<Pair<Float, Float>>> = ArrayList()
    private var animationStopped = false

    // Custom constructors
    constructor(
        context: Context?,
        dotsRadius: Int,
        distanceMultiplier: Int,
        drawOnlyStroke: Boolean,
        strokeWidth: Int,
        firsDotColor: Int,
        secondDotColor: Int,
        thirdDotColor: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.distanceMultiplier = distanceMultiplier
        this.drawOnlyStroke = drawOnlyStroke
        this.strokeWidth = strokeWidth
        this.firsDotColor = firsDotColor
        this.secondDotColor = secondDotColor
        this.thirdDotColor = thirdDotColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initView()
        initInitialValues()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initView()
        initInitialValues()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initView()
        initInitialValues()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initView()
        initInitialValues()
    }


    // Initialization functions
    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AllianceLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            R.styleable.AllianceLoader_alliance_dotsRadius,
            defaultDotsRadius
        )
        this.distanceMultiplier = typedArray.getInteger(
            R.styleable.AllianceLoader_alliance_distanceMultiplier,
            defaultDistanceMultiplier
        )
        this.firsDotColor = typedArray.getColor(
            R.styleable.AllianceLoader_alliance_firstDotsColor,
            defaultFirstColor
        )
        this.secondDotColor = typedArray.getColor(
            R.styleable.AllianceLoader_alliance_secondDotsColor,
            defaultSecondColor
        )
        this.thirdDotColor = typedArray.getColor(
            R.styleable.AllianceLoader_alliance_thirdDotsColor,
            defaultThirdColor
        )
        this.drawOnlyStroke = typedArray.getBoolean(
            R.styleable.AllianceLoader_alliance_drawOnlyStroke,
            defaultDrawOnlyStroke
        )
        if (drawOnlyStroke) {
            this.strokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.AllianceLoader_alliance_strokeWidth,
                defaultStrokeWidth
            )
        }
        this.animDuration =
            typedArray.getInt(R.styleable.AllianceLoader_alliance_animDuration, defaultAnimDuration)

        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.AllianceLoader_alliance_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initView() {

        this.gravity = Gravity.CENTER_HORIZONTAL

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * dotsRadius * distanceMultiplier) + strokeWidth
        }

        firstCircle = CircleView(context, dotsRadius, firsDotColor, drawOnlyStroke, strokeWidth)
        val firstParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        }

        secondCircle = CircleView(context, dotsRadius, secondDotColor, drawOnlyStroke, strokeWidth)
        val secondParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        }

        thirdCircle = CircleView(context, dotsRadius, thirdDotColor, drawOnlyStroke, strokeWidth)
        val thirdParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }

        relativeLayout.addView(firstCircle, firstParam)
        relativeLayout.addView(secondCircle, secondParam)
        relativeLayout.addView(thirdCircle, thirdParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)
        this.addView(relativeLayout, relParam)
    }

    private fun initInitialValues() {
        val fullDistance = (calWidthHeight - ((2 * dotsRadius) + strokeWidth)).toFloat()
        val halfDistance = fullDistance / 2

        val firstPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(halfDistance, fullDistance))
            add(Pair(-halfDistance, fullDistance))
        }
        val secondPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(-fullDistance, 0.0f))
            add(Pair(-halfDistance, -fullDistance))
        }
        val thirdPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(halfDistance, -fullDistance))
            add(Pair(fullDistance, 0.0f))
        }

        posArrayList.add(firstPosArray)
        posArrayList.add(secondPosArray)
        posArrayList.add(thirdPosArray)
    }


    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        val firstCircleAnim = getTranslateAnim(1)
        firstCircle.startAnimation(firstCircleAnim)

        val secondCircleAnim = getTranslateAnim(2)
        secondCircle.startAnimation(secondCircleAnim)

        val thirdCircleAnim = getTranslateAnim(3).apply {
            onAnimationEnd {
                step++
                if (step > 2) {
                    step = 0
                }
                if (!animationStopped) startAnimation()
            }
        }

        thirdCircle.startAnimation(thirdCircleAnim)
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        firstCircle.clearAnimation()
        secondCircle.clearAnimation()
        thirdCircle.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {

        var nextStep = step + 1
        if (nextStep > 2) {
            nextStep = 0
        }

        val fromXPos = posArrayList[circleCount - 1][step].first
        val fromYPos = posArrayList[circleCount - 1][step].second

        val toXPos = posArrayList[circleCount - 1][nextStep].first
        val toYPos = posArrayList[circleCount - 1][nextStep].second

        return TranslateAnimation(fromXPos, toXPos, fromYPos, toYPos).apply {
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
            calWidthHeight = (2 * dotsRadius * distanceMultiplier) + strokeWidth
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