package com.hristogochev.dotloaders.basicviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.utils.getColorResource

/**
 * Created by suneet on 12/29/17.
 *
 * Modified by hristogochev on 02/02/23.
 */
class DotsView : View {
    // Input args
    companion object {
        private const val SIN_45 = 0.7071f
        private const val DOTS_COUNT = 8
    }

    // Default input attributes
    private val defaultDotColor = getColorResource(R.color.loader_defalut)
    private val defaultDotRadius = 30f
    private val defaultRadius = 60f
    private val defaultDotColors =
        IntArray(8) { getColorResource(android.R.color.darker_gray) }

    // Settable attributes
    private var dotColor: Int = defaultDotColor
        set(value) {
            field = value
            dotPaint?.color = value
        }
    private var dotColors = defaultDotColors

    private var dotRadius = defaultDotRadius
        set(value) {
            field = value
            initCoordinates()
        }

    private var radius = defaultRadius

    // Colors
    private var dotPaint: Paint? = null
    private var useMultipleColors = false

    // Dots coordinates
    private lateinit var dotsXCorArr: FloatArray
    private lateinit var dotsYCorArr: FloatArray


    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float,
        radius: Float,
        dotColor: Int
    ) : super(
        context
    ) {
        this.dotRadius = dotRadius
        this.radius = radius
        this.dotColor = dotColor

        initCoordinates()
        initPaints()
    }

    constructor(
        context: Context,
        dotRadius: Float,
        radius: Float,
        dotColors: IntArray
    ) : super(context) {
        this.dotRadius = dotRadius
        this.radius = radius
        this.dotColors = dotColors
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

    private fun initPaints() {
        dotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = dotColor
        }
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = (2 * radius + 2 * dotRadius).toInt()
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until DOTS_COUNT) {
            if (useMultipleColors) {
                dotPaint?.color =
                    if (dotColors.size > i) dotColors[i] else dotColor
            }

            dotPaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], dotRadius, it)
            }
        }
    }
}