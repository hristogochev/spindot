package com.hristogochev.dotloaders.basicviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.hristogochev.dotloaders.R

/**
 * Created by ballu on 13/08/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

class DotView : View {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultDotColor = 0
    private val defaultDrawOnlyStroke = false
    private val defaultStrokeWidth = 0
    private val defaultIsAntiAlias = true

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var dotColor = defaultDotColor
    private var drawOnlyStroke = defaultDrawOnlyStroke
    private var strokeWidth = defaultStrokeWidth
    private var isAntiAlias = defaultIsAntiAlias

    // Paint and coordinates
    private var xyCoordinates: Float = 0.0f
    private lateinit var paint: Paint

    // Custom constructors
    constructor(
        context: Context,
        circleRadius: Float,
        circleColor: Int,
        isAntiAlias: Boolean = true
    ) : super(context) {
        this.dotRadius = circleRadius
        this.dotColor = circleColor
        this.isAntiAlias = isAntiAlias

        initValues()
    }

    constructor(
        context: Context,
        circleRadius: Float,
        circleColor: Int,
        drawOnlyStroke: Boolean,
        strokeWidth: Int
    ) : super(context) {
        this.dotRadius = circleRadius
        this.dotColor = circleColor

        this.drawOnlyStroke = drawOnlyStroke
        this.strokeWidth = strokeWidth

        initValues()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initValues()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initValues()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initValues()
    }


    // Initialization functions
    fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotView, 0, 0)

        try{
            with(typedArray){
                dotRadius =
                    getDimension(
                        R.styleable.DotView_dotRadius,
                        defaultDotRadius
                    )
                dotColor =
                    getColor(R.styleable.DotView_dotColor, defaultDotColor)
                drawOnlyStroke =
                    getBoolean(
                        R.styleable.DotView_dotDrawOnlyStroke,
                        defaultDrawOnlyStroke
                    )
                if (drawOnlyStroke) {
                    strokeWidth =
                        getDimensionPixelSize(
                            R.styleable.DotView_dotStrokeWidth,
                            defaultStrokeWidth
                        )
                }
                isAntiAlias =
                    getBoolean(R.styleable.DotView_dotIsAntiAlias, defaultIsAntiAlias)
            }
        }finally {
            typedArray.recycle()
        }
    }

    private fun initValues() {
        paint = Paint().apply {
            isAntiAlias = this@DotView.isAntiAlias
            if (drawOnlyStroke) {
                style = Paint.Style.STROKE
                strokeWidth = this@DotView.strokeWidth.toFloat()
            } else {
                style = Paint.Style.FILL
            }
            color = dotColor
        }

        xyCoordinates = (dotRadius + (strokeWidth / 2))
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthHeight = ((2 * (dotRadius)) + strokeWidth).toInt()
        setMeasuredDimension(widthHeight, widthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(xyCoordinates, xyCoordinates, dotRadius, paint)
    }
}