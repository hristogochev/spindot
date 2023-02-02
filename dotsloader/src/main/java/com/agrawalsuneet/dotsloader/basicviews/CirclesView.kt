package com.agrawalsuneet.dotsloader.basicviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.utils.getColorResource

/**
 * Created by suneet on 12/29/17.
 * Modified by hristogochev on 02/02/23.
 */
class CirclesView : View {
    // Input args
    companion object {
        private const val SIN_45 = 0.7071f
        private const val DOTS_COUNT = 8
    }

    // Default input attributes
    private val defaultDefaultColor = getColorResource(R.color.loader_defalut)
    private val defaultRadius = 30
    private val defaultBigRadius = 60
    private val defaultDotsColorsArray =
        IntArray(8) { getColorResource(android.R.color.darker_gray) }

    // Settable attributes
    private var defaultColor: Int = defaultDefaultColor
        set(defaultColor) {
            field = defaultColor
            defaultCirclePaint?.color = defaultColor
        }
    private var dotsColorsArray = defaultDotsColorsArray

    private var radius: Int = defaultRadius
        set(radius) {
            field = radius
            initCoordinates()
        }

    private var bigCircleRadius = defaultBigRadius

    // Colors
    private var defaultCirclePaint: Paint? = null
    private var useMultipleColors = false

    // Dots coordinates
    private lateinit var dotsXCorArr: FloatArray
    private lateinit var dotsYCorArr: FloatArray


    // Custom constructors
    constructor(context: Context, dotsRadius: Int, bigCircleRadius: Int, dotsColor: Int) : super(
        context
    ) {
        this.radius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.defaultColor = dotsColor

        initCoordinates()
        initPaints()
    }

    constructor(
        context: Context,
        dotsRadius: Int,
        bigCircleRadius: Int,
        dotsColorsArray: IntArray
    ) : super(context) {
        this.radius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.dotsColorsArray = dotsColorsArray
        this.useMultipleColors = true

        initCoordinates()
        initPaints()
    }


    // Default constructors
    constructor(context: Context) : super(context) {
        initCoordinates()
        initPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initCoordinates()
        initPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initCoordinates()
        initPaints()
    }


    // Initialization functions
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

    private fun initPaints() {
        defaultCirclePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = defaultColor
        }
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * bigCircleRadius + 2 * radius
        setMeasuredDimension(calWidthHeight, calWidthHeight)
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
    }
}