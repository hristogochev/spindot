package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.utils.Helper
import com.agrawalsuneet.dotsloader.utils.getActivity
import com.agrawalsuneet.dotsloader.utils.getColorResource
import java.util.*

/**
 * Created by ballu on 04/07/17.
 */

class CircularDotsLoader : View {
    // Input args
    companion object {
        private const val SIN_45 = 0.7071f
        private const val DOTS_COUNT = 8
    }

    // Default input attributes
    private val defaultDefaultColor = getColorResource(R.color.loader_defalut)
    private val defaultSelectedColor = getColorResource(R.color.loader_selected)
    private val defaultRadius = 30
    private val defaultAnimDur = 500
    private val defaultShowRunningShadow = true
    private val defaultFirstShadowColor = 0
    private val defaultSecondShadowColor = 0
    private val defaultBigCircleRadius = 60

    // Settable attributes
    private var defaultColor = defaultDefaultColor
        set(defaultColor) {
            field = defaultColor
            defaultCirclePaint?.color = defaultColor
        }

    private var selectedColor = defaultSelectedColor
        set(selectedColor) {
            field = selectedColor
            selectedCirclePaint?.let {
                it.color = selectedColor
                initShadowDotsPaints()
            }
        }

    private var radius = defaultRadius
        set(radius) {
            field = radius
            initCoordinates()
        }

    private var animDur = defaultAnimDur

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

    private var bigCircleRadius = defaultBigCircleRadius

    // Colors
    private var defaultCirclePaint: Paint? = null
    private var selectedCirclePaint: Paint? = null
    private var useMultipleColors = false
    private lateinit var firstShadowPaint: Paint
    private lateinit var secondShadowPaint: Paint
    private var isShadowColorSet = false
    private var dotsColorsArray =
        IntArray(8) { getColorResource(android.R.color.darker_gray) }

    // Dots coordinates
    private lateinit var dotsXCorArr: FloatArray
    private lateinit var dotsYCorArr: FloatArray

    // Animation attributes
    var startAndStopAnimationOnVisibilityChange = true
        set(value) {
            field = value
            invalidate()
        }

    private var animationTimer: Timer? = null

    private var selectedDotPos = 1

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
    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularDotsLoader, 0, 0)

        this.defaultColor =
            typedArray.getColor(
                R.styleable.CircularDotsLoader_circularDots_defaultColor,
                defaultDefaultColor
            )
        this.selectedColor =
            typedArray.getColor(
                R.styleable.CircularDotsLoader_circularDots_selectedColor,
                defaultSelectedColor
            )
        this.radius =
            typedArray.getDimensionPixelSize(
                R.styleable.CircularDotsLoader_circularDots_circleRadius,
                defaultRadius
            )

        this.animDur =
            typedArray.getInt(
                R.styleable.CircularDotsLoader_circularDots_animDur,
                defaultAnimDur
            )

        this.showRunningShadow =
            typedArray.getBoolean(
                R.styleable.CircularDotsLoader_circularDots_showRunningShadow,
                defaultShowRunningShadow
            )

        this.firstShadowColor =
            typedArray.getColor(
                R.styleable.CircularDotsLoader_circularDots_firstShadowColor,
                defaultFirstShadowColor
            )
        this.secondShadowColor =
            typedArray.getColor(
                R.styleable.CircularDotsLoader_circularDots_secondShadowColor,
                defaultSecondShadowColor
            )

        this.bigCircleRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.CircularDotsLoader_circularDots_bigCircleRadius,
                defaultBigCircleRadius
            )

        typedArray.recycle()
    }

    private fun initCoordinates() {
        val sin45Radius = SIN_45 * bigCircleRadius

        dotsXCorArr = FloatArray(DOTS_COUNT)
        dotsYCorArr = FloatArray(DOTS_COUNT)

        for (i in 0 until DOTS_COUNT) {
            dotsYCorArr[i] = (bigCircleRadius + radius).toFloat()
            dotsXCorArr[i] = dotsYCorArr[i]
        }

        dotsXCorArr[1] = dotsXCorArr[1] + sin45Radius
        dotsXCorArr[2] = dotsXCorArr[2] + bigCircleRadius
        dotsXCorArr[3] = dotsXCorArr[3] + sin45Radius

        dotsXCorArr[5] = dotsXCorArr[5] - sin45Radius
        dotsXCorArr[6] = dotsXCorArr[6] - bigCircleRadius
        dotsXCorArr[7] = dotsXCorArr[7] - sin45Radius

        dotsYCorArr[0] = dotsYCorArr[0] - bigCircleRadius
        dotsYCorArr[1] = dotsYCorArr[1] - sin45Radius
        dotsYCorArr[3] = dotsYCorArr[3] + sin45Radius

        dotsYCorArr[4] = dotsYCorArr[4] + bigCircleRadius
        dotsYCorArr[5] = dotsYCorArr[5] + sin45Radius
        dotsYCorArr[7] = dotsYCorArr[7] - sin45Radius
    }

    private fun initDotPaints() {
        defaultCirclePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = defaultColor
        }
        selectedCirclePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = selectedColor
        }
    }

    private fun initShadowDotsPaints() {
        if (showRunningShadow) {
            if (!isShadowColorSet) {
                firstShadowColor = Helper.adjustAlpha(selectedColor, 0.7f)
                secondShadowColor = Helper.adjustAlpha(selectedColor, 0.5f)
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

    // Animation timer controls
    fun startAnimationTimer() {
        if (animationTimer != null) return

        animationTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    selectedDotPos++

                    if (selectedDotPos > DOTS_COUNT) {
                        selectedDotPos = 1
                    }

                    context.getActivity()?.runOnUiThread { invalidate() }
                }
            }, 0, animDur.toLong())
        }
    }

    fun stopAnimationTimer() {
        if (animationTimer == null) return

        animationTimer?.cancel()
        animationTimer = null
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = (2 * bigCircleRadius + 2 * radius)
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (!startAndStopAnimationOnVisibilityChange) return

        if (visibility != VISIBLE) {
            stopAnimationTimer()
        } else {
            startAnimationTimer()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until DOTS_COUNT) {
            if (useMultipleColors) {
                defaultCirclePaint?.color =
                    if (dotsColorsArray.size > i) dotsColorsArray[i] else defaultColor
            }

            defaultCirclePaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), it)
            }
        }
        drawCircles(canvas)
    }

    private fun drawCircles(canvas: Canvas) {
        val firstShadowPos = if (selectedDotPos == 1) 8 else selectedDotPos - 1
        val secondShadowPos = if (firstShadowPos == 1) 8 else firstShadowPos - 1

        for (i in 0 until DOTS_COUNT) {

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    radius.toFloat(),
                    selectedCirclePaint!!
                )
            } else if (this.showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    radius.toFloat(),
                    firstShadowPaint
                )
            } else if (this.showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    radius.toFloat(),
                    secondShadowPaint
                )
            } else {
                canvas.drawCircle(
                    dotsXCorArr[i],
                    dotsYCorArr[i],
                    radius.toFloat(),
                    defaultCirclePaint!!
                )
            }
        }
    }
}
