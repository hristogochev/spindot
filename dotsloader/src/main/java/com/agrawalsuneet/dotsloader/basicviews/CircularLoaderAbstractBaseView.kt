package com.agrawalsuneet.dotsloader.basicviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.utils.Helper

abstract class CircularLoaderAbstractBaseView : View {
    var animDur = 500

    protected var selectedDotPos = 1

    protected var logTime: Long = 0

    protected var defaultCirclePaint: Paint? = null
    protected var selectedCirclePaint: Paint? = null

    protected lateinit var firstShadowPaint: Paint
    protected lateinit var secondShadowPaint: Paint

    lateinit var dotsXCorArr: FloatArray

    protected var shouldAnimate = true

    var radius: Int = 30
        set(radius) {
            field = radius
            initCordinates()
        }

    protected val noOfDots = 8
    private val SIN_45 = 0.7071f

    lateinit var dotsYCorArr: FloatArray

    private var isShadowColorSet = false


    var bigCircleRadius: Int = 60

    var useMultipleColors: Boolean = false
    var dotsColorsArray = IntArray(8) { resources.getColor(android.R.color.darker_gray) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun initCordinates() {
        val sin45Radius = SIN_45 * bigCircleRadius

        dotsXCorArr = FloatArray(noOfDots)
        dotsYCorArr = FloatArray(noOfDots)

        for (i in 0 until noOfDots) {
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

    open fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotsLoaderBaseView, 0, 0)

        this.defaultColor = typedArray.getColor(
            R.styleable.DotsLoaderBaseView_loader_defaultColor,
            resources.getColor(R.color.loader_defalut)
        )
        this.selectedColor = typedArray.getColor(
            R.styleable.DotsLoaderBaseView_loader_selectedColor,
            resources.getColor(R.color.loader_selected)
        )

        this.radius =
            typedArray.getDimensionPixelSize(R.styleable.DotsLoaderBaseView_loader_circleRadius, 30)

        this.animDur = typedArray.getInt(R.styleable.DotsLoaderBaseView_loader_animDur, 500)

        this.showRunningShadow =
            typedArray.getBoolean(R.styleable.DotsLoaderBaseView_loader_showRunningShadow, true)

        this.firstShadowColor =
            typedArray.getColor(R.styleable.DotsLoaderBaseView_loader_firstShadowColor, 0)
        this.secondShadowColor =
            typedArray.getColor(R.styleable.DotsLoaderBaseView_loader_secondShadowColor, 0)

        typedArray.recycle()
    }


    //init paints for drawing dots
    fun initPaints() {
        defaultCirclePaint = Paint()
        defaultCirclePaint?.isAntiAlias = true
        defaultCirclePaint?.style = Paint.Style.FILL
        defaultCirclePaint?.color = defaultColor

        selectedCirclePaint = Paint()
        selectedCirclePaint?.isAntiAlias = true
        selectedCirclePaint?.style = Paint.Style.FILL
        selectedCirclePaint?.color = selectedColor
    }

    fun startAnimation() {
        shouldAnimate = true
        invalidate()
    }

    fun stopAnimation() {
        shouldAnimate = false
        invalidate()
    }

    //init paints for drawing shadow dots
    fun initShadowPaints() {
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


    var defaultColor: Int = resources.getColor(android.R.color.darker_gray)
        set(defaultColor) {
            field = defaultColor
            defaultCirclePaint?.color = defaultColor
        }

    open var selectedColor: Int = resources.getColor(R.color.loader_selected)
        set(selectedColor) {
            field = selectedColor
            selectedCirclePaint?.let {
                it.color = selectedColor
                initShadowPaints()
            }
        }


    var showRunningShadow: Boolean = true

    var firstShadowColor: Int = 0
        set(value) {
            field = value
            if (value != 0) {
                isShadowColorSet = true
                initShadowPaints()
            }
        }


    var secondShadowColor: Int = 0
        set(value) {
            field = value
            if (value != 0) {
                isShadowColorSet = true
                initShadowPaints()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * bigCircleRadius + 2 * radius
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until noOfDots) {

            if (useMultipleColors) {
                defaultCirclePaint?.color =
                    if (dotsColorsArray.size > i) dotsColorsArray[i] else defaultColor
            }

            defaultCirclePaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), it)
            }
        }
    }
}