package com.agrawalsuneet.dotsloader.basicviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.contracts.InitializationContract

/**
 * Created by ballu on 13/08/17.
 * Modified by hristogochev on 02/02/23.
 */

class CircleView : View, InitializationContract {

    // Default input attributes
    private val defaultCircleRadius = 30
    private val defaultCircleColor = 0
    private val defaultDrawOnlyStroke = false
    private val defaultStrokeWidth = 0
    private val defaultIsAntiAlias = true

    // Settable attributes
    private var circleRadius = defaultCircleRadius
    private var circleColor = defaultCircleColor
    private var drawOnlyStroke = defaultDrawOnlyStroke
    private var strokeWidth = defaultStrokeWidth
    private var isAntiAlias = defaultIsAntiAlias

    // Paint and coordinates
    private var xyCoordinates: Float = 0.0f
    private val paint: Paint = Paint()

    // Custom constructors
    constructor(
        context: Context,
        circleRadius: Int,
        circleColor: Int,
        isAntiAlias: Boolean = true
    ) : super(context) {
        this.circleRadius = circleRadius
        this.circleColor = circleColor
        this.isAntiAlias = isAntiAlias

        initValues()
    }

    constructor(
        context: Context,
        circleRadius: Int,
        circleColor: Int,
        drawOnlyStroke: Boolean,
        strokeWidth: Int
    ) : super(context) {
        this.circleRadius = circleRadius
        this.circleColor = circleColor

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
    override fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0)

        this.circleRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.CircleView_circleRadius,
                defaultCircleRadius
            )
        this.circleColor =
            typedArray.getColor(R.styleable.CircleView_circleColor, defaultCircleColor)
        this.drawOnlyStroke =
            typedArray.getBoolean(
                R.styleable.CircleView_circleDrawOnlyStroke,
                defaultDrawOnlyStroke
            )
        if (drawOnlyStroke) {
            this.strokeWidth =
                typedArray.getDimensionPixelSize(
                    R.styleable.CircleView_circleStrokeWidth,
                    defaultStrokeWidth
                )
        }
        this.isAntiAlias =
            typedArray.getBoolean(R.styleable.CircleView_circleIsAntiAlias, defaultIsAntiAlias)

        typedArray.recycle()
    }

    override fun initViews() {}
    private fun initValues() {
        paint.isAntiAlias = isAntiAlias

        if (drawOnlyStroke) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth.toFloat()
        } else {
            paint.style = Paint.Style.FILL
        }
        paint.color = circleColor

        //adding half of strokeWidth because
        //the stroke will be half inside the drawing circle and half outside
        xyCoordinates = (circleRadius + (strokeWidth / 2)).toFloat()
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthHeight = (2 * (circleRadius)) + strokeWidth
        setMeasuredDimension(widthHeight, widthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(xyCoordinates, xyCoordinates, circleRadius.toFloat(), paint)
    }
}