package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationView
import com.hristogochev.dotloaders.utils.adjustAlpha
import com.hristogochev.dotloaders.utils.getActivity
import com.hristogochev.dotloaders.utils.getColorResource

/**
 * Created by ballu on 04/07/17.
 *
 * Modified by hristogochev on 01/02/23.
 */

class ClassicLoader : AnimationView {
    // Input args
    companion object {
        private const val SIN_45 = 0.7071f
        private const val DOTS_COUNT = 8
    }

    // Default input attributes
    private val defaultInactiveColor = getColorResource(R.color.loader_default)
    private val defaultActiveColor = getColorResource(R.color.loader_selected)
    private val defaultDotRadius = 42f
    private val defaultAnimDuration: Long = 500
    private val defaultShowRunningShadow = true
    private val defaultFirstShadowColor = 0
    private val defaultSecondShadowColor = 0
    private val defaultRadius = 126f

    // Settable attributes
    private var inactiveColor = defaultInactiveColor
        set(value) {
            field = value
            inactiveDotPaint?.color = value
        }
    private var activeColor = defaultActiveColor
        set(value) {
            field = value
            activeCirclePaint?.let {
                it.color = value
                initShadowDotsPaints()
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
                initShadowDotsPaints()
            }
        }
    private var secondShadowColor = defaultSecondShadowColor
        set(value) {
            field = value
            if (value != 0) {
                isShadowColorSet = true
                initShadowDotsPaints()
            }
        }
    private var radius = defaultRadius

    // Colors
    private var inactiveDotPaint: Paint? = null
    private var activeCirclePaint: Paint? = null
    private lateinit var firstShadowPaint: Paint
    private lateinit var secondShadowPaint: Paint
    private var isShadowColorSet = false

    // Dots coordinates
    private lateinit var dotsXCorArr: FloatArray
    private lateinit var dotsYCorArr: FloatArray

    // Animation attributes
    private var selectedDotPos = 1


    // Custom constructors
    constructor(
        context: Context,
        inactiveColor: Int? = null,
        activeColor: Int? = null,
        dotRadius: Float? = null,
        radius: Float? = null,
        animDuration: Long? = null,
        showRunningShadow: Boolean? = null,
        firstShadowColor: Int? = null,
        secondShadowColor: Int? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.inactiveColor = inactiveColor ?: defaultInactiveColor
        this.activeColor = activeColor ?: defaultActiveColor
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.radius = radius ?: defaultRadius
        this.animDuration = animDuration ?: defaultAnimDuration
        this.showRunningShadow = showRunningShadow ?: defaultShowRunningShadow
        this.firstShadowColor = firstShadowColor ?: defaultFirstShadowColor
        this.secondShadowColor = secondShadowColor ?: defaultSecondShadowColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initCoordinates()
        initDotPaints()
        initShadowDotsPaints()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initCoordinates()
        initDotPaints()
        initShadowDotsPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initCoordinates()
        initDotPaints()
        initShadowDotsPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initCoordinates()
        initDotPaints()
        initShadowDotsPaints()
    }

    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClassicLoader, 0, 0)

        try {
            with(typedArray) {
                inactiveColor =
                    getColor(
                        R.styleable.ClassicLoader_classic_inactiveColor,
                        defaultInactiveColor
                    )
                activeColor =
                    getColor(
                        R.styleable.ClassicLoader_classic_activeColor,
                        defaultActiveColor
                    )
                dotRadius =
                    getDimension(
                        R.styleable.ClassicLoader_classic_dotRadius,
                        defaultDotRadius
                    )

                animDuration =
                    getInt(
                        R.styleable.ClassicLoader_classic_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()

                showRunningShadow =
                    getBoolean(
                        R.styleable.ClassicLoader_classic_showRunningShadow,
                        defaultShowRunningShadow
                    )

                firstShadowColor =
                    getColor(
                        R.styleable.ClassicLoader_classic_firstShadowColor,
                        defaultFirstShadowColor
                    )
                secondShadowColor =
                    getColor(
                        R.styleable.ClassicLoader_classic_secondShadowColor,
                        defaultSecondShadowColor
                    )

                radius =
                    getDimension(
                        R.styleable.ClassicLoader_classic_radius,
                        defaultRadius
                    )
            }
        } finally {
            typedArray.recycle()
        }
    }


    private fun initCoordinates() {
        val sin45Radius = SIN_45 * radius

        dotsXCorArr = FloatArray(DOTS_COUNT)
        dotsYCorArr = FloatArray(DOTS_COUNT)

        for (i in 0 until DOTS_COUNT) {
            dotsYCorArr[i] = (radius + dotRadius)
            dotsXCorArr[i] = dotsYCorArr[i]
        }

        dotsXCorArr[1] = dotsXCorArr[1] + sin45Radius
        dotsXCorArr[2] = dotsXCorArr[2] + radius
        dotsXCorArr[3] = dotsXCorArr[3] + sin45Radius

        dotsXCorArr[5] = dotsXCorArr[5] - sin45Radius
        dotsXCorArr[6] = dotsXCorArr[6] - radius
        dotsXCorArr[7] = dotsXCorArr[7] - sin45Radius

        dotsYCorArr[0] = dotsYCorArr[0] - radius
        dotsYCorArr[1] = dotsYCorArr[1] - sin45Radius
        dotsYCorArr[3] = dotsYCorArr[3] + sin45Radius

        dotsYCorArr[4] = dotsYCorArr[4] + radius
        dotsYCorArr[5] = dotsYCorArr[5] + sin45Radius
        dotsYCorArr[7] = dotsYCorArr[7] - sin45Radius
    }

    private fun initDotPaints() {
        inactiveDotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = inactiveColor
        }
        activeCirclePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = activeColor
        }
    }

    private fun initShadowDotsPaints() {
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
        selectedDotPos++

        if (selectedDotPos > DOTS_COUNT) selectedDotPos = 1

        context.getActivity()?.runOnUiThread { invalidate() }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = ((2 * radius + 2 * dotRadius)).toInt()
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until DOTS_COUNT) {
            inactiveDotPaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], dotRadius, it)
            }
        }
        drawCircles(canvas)
    }

    // Utility functions
    private fun drawCircles(canvas: Canvas) {
        val firstShadowPos = if (selectedDotPos == 1) 8 else selectedDotPos - 1
        val secondShadowPos = if (firstShadowPos == 1) 8 else firstShadowPos - 1

        for (i in 0 until DOTS_COUNT) {

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    dotRadius,
                    activeCirclePaint!!
                )
            } else if (this.showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    dotRadius,
                    firstShadowPaint
                )
            } else if (this.showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    dotRadius,
                    secondShadowPaint
                )
            } else {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    dotRadius,
                    inactiveDotPaint!!
                )
            }
        }
    }
}
