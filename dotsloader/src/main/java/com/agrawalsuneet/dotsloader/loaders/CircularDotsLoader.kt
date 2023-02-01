package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.utils.Helper
import com.agrawalsuneet.dotsloader.utils.Utils
import com.agrawalsuneet.dotsloader.utils.getActivity
import java.util.*

/**
 * Created by ballu on 04/07/17.
 */

class CircularDotsLoader : View {

    // Default input attributes
    private val defaultDefaultColor = resources.getColor(R.color.loader_defalut)
    private val defaultSelectedColor = resources.getColor(R.color.loader_selected)
    private val defaultRadius = 30
    private val defaultAnimDur = 500
    private val defaultShowRunningShadow = true
    private val defaultFirstShadowColor = 0
    private val defaultSecondShadowColor = 0
    private val defaultBigCircleRadius = 60

    // Settable attributes
    private var defaultColor = resources.getColor(android.R.color.darker_gray)
        set(defaultColor) {
            field = defaultColor
            defaultCirclePaint?.color = defaultColor
        }

    private var selectedColor = resources.getColor(R.color.loader_selected)
        set(selectedColor) {
            field = selectedColor
            selectedCirclePaint?.let {
                it.color = selectedColor
                initShadowDotsPaints()
            }
        }

    private var radius: Int = defaultRadius
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

    // Animation attributes
    private var logTime: Long = 0

    private var animationTimer: Timer? = null
    private var shouldAnimate = true

    private var dotsColorsArray = IntArray(8) { resources.getColor(android.R.color.darker_gray) }
    private var defaultCirclePaint: Paint? = null
    private var selectedCirclePaint: Paint? = null
    private var useMultipleColors: Boolean = false

    private var isShadowColorSet = false
    private lateinit var firstShadowPaint: Paint
    private lateinit var secondShadowPaint: Paint

    private var selectedDotPos = 1

    private lateinit var dotsXCorArr: FloatArray
    private lateinit var dotsYCorArr: FloatArray


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
            dotsYCorArr[i] = (this.bigCircleRadius + radius).toFloat()
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

        dotsYCorArr[4] = dotsYCorArr[4] + this.bigCircleRadius
        dotsYCorArr[5] = dotsYCorArr[5] + sin45Radius
        dotsYCorArr[7] = dotsYCorArr[7] - sin45Radius
    }

    private fun initDotPaints() {
        defaultCirclePaint = Paint()
        defaultCirclePaint?.isAntiAlias = true
        defaultCirclePaint?.style = Paint.Style.FILL
        defaultCirclePaint?.color = defaultColor

        selectedCirclePaint = Paint()
        selectedCirclePaint?.isAntiAlias = true
        selectedCirclePaint?.style = Paint.Style.FILL
        selectedCirclePaint?.color = selectedColor
    }

    private fun initShadowDotsPaints() {
        if (showRunningShadow) {
            if (!isShadowColorSet) {
                firstShadowColor = Helper.adjustAlpha(selectedColor, 0.7f)
                secondShadowColor = Helper.adjustAlpha(selectedColor, 0.5f)
                isShadowColorSet = true
            }

            firstShadowPaint = Paint()
            firstShadowPaint.isAntiAlias = true
            firstShadowPaint.style = Paint.Style.FILL
            firstShadowPaint.color = firstShadowColor

            secondShadowPaint = Paint()
            secondShadowPaint.isAntiAlias = true
            secondShadowPaint.style = Paint.Style.FILL
            secondShadowPaint.color = secondShadowColor
        }
    }

    fun startAnimation() {
        shouldAnimate = true
        invalidate()
    }

    fun stopAnimation() {
        shouldAnimate = false
        invalidate()
    }

    private fun startAnimationTimer() {
        animationTimer = Timer()
        animationTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                selectedDotPos++

                if (selectedDotPos > DOTS_COUNT) {
                    selectedDotPos = 1
                }

                context.getActivity()?.runOnUiThread { invalidate() }
            }
        }, 0, animDur.toLong())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * bigCircleRadius + 2 * radius
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {

            if (useMultipleColors) {
                defaultCirclePaint?.color =
                    if (dotsColorsArray.size > i) dotsColorsArray[i] else defaultColor
            }

            defaultCirclePaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), it)
            }
        }
        drawCircle(canvas)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != VISIBLE) {
            animationTimer?.cancel()
        } else if (shouldAnimate) {
            startAnimationTimer()
        }
    }

    private fun drawCircle(canvas: Canvas) {
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

    companion object {
        private const val SIN_45 = 0.7071f
        private const val DOTS_COUNT = 8
    }
}
