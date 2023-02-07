package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationView
import com.hristogochev.dotloaders.utils.adjustAlpha
import com.hristogochev.dotloaders.utils.getActivity
import com.hristogochev.dotloaders.utils.getColorResource

/**
 * Created by ballu on 04/07/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

class LinearLoader : AnimationView {

    // Default input attributes
    private val defaultInactiveColor = getColorResource(R.color.loader_default)
    private val defaultActiveColor = getColorResource(R.color.loader_selected)
    private val defaultDotRadius = 30f
    private val defaultAnimDuration: Long = 500
    private val defaultShowRunningShadow = true
    private val defaultFirstShadowColor = 0
    private val defaultSecondShadowColor = 0
    private val defaultDotCount = 3
    private val defaultExpandedLeadingDotRadiusAddition = 10f
    private val defaultSpacing = 15
    private val defaultSingleDirection = false
    private val defaultExpandLeadingDot = false

    // Settable attributes
    private var inactiveColor = defaultInactiveColor
        set(defaultColor) {
            field = defaultColor
            inactiveDotPaint?.color = defaultColor
        }
    private var activeColor = defaultActiveColor
        set(selectedColor) {
            field = selectedColor
            activeDotPaint?.let {
                it.color = selectedColor
                initShadowPaints()
            }
        }
    private var dotRadius = defaultDotRadius
        set(value) {
            field = value
            initCoordinates()
        }
    override var animDuration = defaultAnimDuration
    private var showRunningShadow = defaultShowRunningShadow
    private var firstShadowColor = defaultFirstShadowColor
        set(value) {
            field = value
            if (value != 0) {
                isShadowColorSet = true
                initShadowPaints()
            }
        }
    private var secondShadowColor = defaultSecondShadowColor
        set(value) {
            field = value
            if (value != 0) {
                isShadowColorSet = true
                initShadowPaints()
            }
        }
    private var dotCount = defaultDotCount
        set(noOfDots) {
            field = noOfDots
            initCoordinates()
        }
    private var expandedLeadingDotRadius =
        defaultDotRadius + defaultExpandedLeadingDotRadiusAddition
        set(selRadius) {
            field = selRadius
            initCoordinates()
        }
    private var spacing = defaultSpacing
        set(value) {
            field = value
            initCoordinates()
        }
    private var singleDirection = defaultSingleDirection
    private var expandLeadingDot = defaultExpandLeadingDot
        set(expandOnSelect) {
            field = expandOnSelect
            initCoordinates()
        }

    // Colors
    private var inactiveDotPaint: Paint? = null
    private var activeDotPaint: Paint? = null
    private lateinit var firstShadowPaint: Paint
    private lateinit var secondShadowPaint: Paint
    private var isShadowColorSet = false

    // Coordinates
    private lateinit var dotsXCorArr: FloatArray

    // Animation
    private var selectedDotPos = 1
    private var diffRadius: Float = 0f
    private var isFwdDir = true

    constructor(
        context: Context,
        inactiveColor: Int? = null,
        activeColor: Int? = null,
        dotRadius: Float? = null,
        animDuration: Long? = null,
        showRunningShadow: Boolean? = null,
        firstShadowColor: Int? = null,
        secondShadowColor: Int? = null,
        dotCount: Int? = null,
        expandedLeadingDotRadius: Float? = null,
        spacing: Int? = null,
        singleDirection: Boolean? = null,
        expandLeadingDot: Boolean? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.inactiveColor = inactiveColor ?: defaultInactiveColor
        this.activeColor = activeColor ?: defaultActiveColor
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.animDuration = animDuration ?: defaultAnimDuration
        this.showRunningShadow = showRunningShadow ?: defaultShowRunningShadow
        this.firstShadowColor = firstShadowColor ?: defaultFirstShadowColor
        this.secondShadowColor = secondShadowColor ?: defaultSecondShadowColor
        this.dotCount = dotCount ?: defaultDotCount
        this.expandedLeadingDotRadius =
            expandedLeadingDotRadius
                ?: (this.defaultDotRadius + defaultExpandedLeadingDotRadiusAddition)
        this.spacing = spacing ?: defaultSpacing
        this.singleDirection = singleDirection ?: defaultSingleDirection
        this.expandLeadingDot = expandLeadingDot ?: defaultExpandLeadingDot
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context) : super(context) {
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearLoader, 0, 0)

        try {
            with(typedArray) {
                inactiveColor = getColor(
                    R.styleable.LinearLoader_linear_inactiveColor,
                    defaultInactiveColor
                )
                activeColor = getColor(
                    R.styleable.LinearLoader_linear_activeColor,
                    defaultActiveColor
                )
                dotRadius =
                    getDimension(
                        R.styleable.LinearLoader_linear_dotRadius,
                        defaultDotRadius
                    )
                animDuration =
                    getInt(
                        R.styleable.LinearLoader_linear_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
                showRunningShadow =
                    getBoolean(
                        R.styleable.LinearLoader_linear_showRunningShadow,
                        defaultShowRunningShadow
                    )
                firstShadowColor =
                    getColor(
                        R.styleable.LinearLoader_linear_firstShadowColor,
                        defaultFirstShadowColor
                    )
                secondShadowColor =
                    getColor(
                        R.styleable.LinearLoader_linear_secondShadowColor,
                        defaultSecondShadowColor
                    )
                dotCount =
                    getInt(R.styleable.LinearLoader_linear_dotCount, defaultDotCount)
                expandedLeadingDotRadius = getDimension(
                    R.styleable.LinearLoader_linear_expandedLeadingDotRadius,
                    dotRadius + defaultExpandedLeadingDotRadiusAddition
                )
                spacing =
                    getDimensionPixelSize(
                        R.styleable.LinearLoader_linear_spacing,
                        defaultSpacing
                    )
                singleDirection =
                    getBoolean(
                        R.styleable.LinearLoader_linear_singleDirection,
                        defaultSingleDirection
                    )
                expandLeadingDot =
                    getBoolean(
                        R.styleable.LinearLoader_linear_expandLeadingDot,
                        defaultExpandLeadingDot
                    )
            }
        } finally {
            typedArray.recycle()
        }
    }

    private fun initCoordinates() {
        diffRadius = expandedLeadingDotRadius - dotRadius

        dotsXCorArr = FloatArray(this.dotCount)

        for (i in 0 until dotCount) {
            dotsXCorArr[i] = (i * spacing + (i * 2 + 1) * dotRadius)
        }
    }

    private fun initPaints() {
        inactiveDotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = inactiveColor
        }
        activeDotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = activeColor
        }
    }

    private fun initShadowPaints() {
        if (showRunningShadow) {
            if (!isShadowColorSet) {
                firstShadowColor = adjustAlpha(activeColor, 0.7f)
                secondShadowColor = adjustAlpha(activeColor, 0.5f)
                isShadowColorSet = true
            }
            firstShadowPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = firstShadowColor
            }
            secondShadowPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = secondShadowColor
            }
        }
    }

    // Animation controls
    override fun playAnimationLoop() {
        if (singleDirection) {
            selectedDotPos++
            if (selectedDotPos > dotCount) {
                selectedDotPos = 1
            }
        } else {
            if (isFwdDir) {
                selectedDotPos++
                if (selectedDotPos == dotCount) {
                    isFwdDir = false
                }
            } else {
                selectedDotPos--
                if (selectedDotPos == 1) {
                    isFwdDir = true
                }
            }
        }

        context.getActivity()?.runOnUiThread { invalidate() }
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth: Int
        val calHeight: Int

        if (expandLeadingDot) {
            calWidth =
                (2 * this.dotCount * dotRadius + (this.dotCount - 1) * spacing + 2 * diffRadius).toInt()
            calHeight = (2 * this.expandedLeadingDotRadius).toInt()
        } else {
            calHeight = (2 * dotRadius).toInt()
            calWidth = ((2 * this.dotCount * dotRadius + (this.dotCount - 1) * spacing)).toInt()
        }
        setMeasuredDimension(calWidth, calHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }


    // Utility functions
    private fun drawCircle(canvas: Canvas) {
        for (i in 0 until dotCount) {

            var xCor = dotsXCorArr[i]
            if (expandLeadingDot) {
                if (i + 1 == selectedDotPos) {
                    xCor += diffRadius
                } else if (i + 1 > selectedDotPos) {
                    xCor += (2 * diffRadius)
                }
            }

            var firstShadowPos: Int
            var secondShadowPos: Int

            if ((isFwdDir && selectedDotPos > 1) || selectedDotPos == dotCount) {
                firstShadowPos = selectedDotPos - 1
                secondShadowPos = firstShadowPos - 1
            } else {
                firstShadowPos = selectedDotPos + 1
                secondShadowPos = firstShadowPos + 1
            }

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandLeadingDot) this.expandedLeadingDotRadius else dotRadius),
                    (if (expandLeadingDot) this.expandedLeadingDotRadius else dotRadius),
                    activeDotPaint!!
                )
            } else if (showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandLeadingDot) this.expandedLeadingDotRadius else dotRadius),
                    dotRadius,
                    firstShadowPaint
                )
            } else if (showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandLeadingDot) this.expandedLeadingDotRadius else dotRadius),
                    dotRadius,
                    secondShadowPaint
                )
            } else {
                canvas.drawCircle(
                    xCor,
                    (if (expandLeadingDot) this.expandedLeadingDotRadius else dotRadius),
                    dotRadius,
                    inactiveDotPaint!!
                )
            }
        }
    }
}
