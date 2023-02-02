package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.contracts.InitializationContract
import com.agrawalsuneet.dotsloader.utils.Helper
import com.agrawalsuneet.dotsloader.utils.Utils
import com.agrawalsuneet.dotsloader.utils.getColorResource
import java.util.*

/**
 * Created by ballu on 04/07/17.
 * Modified by hristogochev on 02/02/23.
 */

class LinearDotsLoader : View,InitializationContract, AnimationContract {

    // Default input attributes
    private val defaultDefaultColor = getColorResource(R.color.loader_defalut)
    private val defaultSelectedColor = getColorResource(R.color.loader_selected)
    private val defaultRadius = 30
    private val defaultAnimDur = 500
    private val defaultShowRunningShadow = true
    private val defaultFirstShadowColor = 0
    private val defaultSecondShadowColor = 0
    private val defaultNoOfDots = 3
    private val defaultSelRadiusAddition = 10
    private val defaultDotsDistance = 15
    private val defaultIsSingleDir = false
    private val defaultExpandOnSelected = false
    private val defaultToggleOnVisibilityChange = true

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
                initShadowPaints()
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
    private var noOfDots = defaultNoOfDots
        set(noOfDots) {
            field = noOfDots
            initCoordinates()
        }
    private var selRadius = defaultRadius + defaultSelRadiusAddition
        set(selRadius) {
            field = selRadius
            initCoordinates()
        }
    private var dotsDistance = defaultDotsDistance
        set(value) {
            field = value
            initCoordinates()
        }
    private var isSingleDir = defaultIsSingleDir
    private var expandOnSelect = defaultExpandOnSelected
        set(expandOnSelect) {
            field = expandOnSelect
            initCoordinates()
        }
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Colors
    private var defaultCirclePaint: Paint? = null
    private var selectedCirclePaint: Paint? = null
    private lateinit var firstShadowPaint: Paint
    private lateinit var secondShadowPaint: Paint
    private var isShadowColorSet = false

    // Coordinates
    private lateinit var dotsXCorArr: FloatArray

    // Animation
    private var animationTimer: Timer? = null
    private var selectedDotPos = 1
    private var diffRadius: Int = 0
    private var isFwdDir = true

    constructor(context: Context) : super(context) {
        initCoordinates()
        initPaints()
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearDotsLoader, 0, 0)

        this.defaultColor = typedArray.getColor(
            R.styleable.LinearDotsLoader_loader_defaultColor,
            defaultDefaultColor
        )
        this.selectedColor = typedArray.getColor(
            R.styleable.LinearDotsLoader_loader_selectedColor,
            defaultSelectedColor
        )
        this.radius =
            typedArray.getDimensionPixelSize(
                R.styleable.LinearDotsLoader_loader_circleRadius,
                defaultRadius
            )
        this.animDur =
            typedArray.getInt(R.styleable.LinearDotsLoader_loader_animDur, defaultAnimDur)
        this.showRunningShadow =
            typedArray.getBoolean(
                R.styleable.LinearDotsLoader_loader_showRunningShadow,
                defaultShowRunningShadow
            )
        this.firstShadowColor =
            typedArray.getColor(
                R.styleable.LinearDotsLoader_loader_firstShadowColor,
                defaultFirstShadowColor
            )
        this.secondShadowColor =
            typedArray.getColor(
                R.styleable.LinearDotsLoader_loader_secondShadowColor,
                defaultSecondShadowColor
            )
        this.noOfDots =
            typedArray.getInt(R.styleable.LinearDotsLoader_loader_noOfDots, defaultNoOfDots)
        this.selRadius = typedArray.getDimensionPixelSize(
            R.styleable.LinearDotsLoader_loader_selectedRadius,
            radius + defaultSelRadiusAddition
        )
        this.dotsDistance =
            typedArray.getDimensionPixelSize(
                R.styleable.LinearDotsLoader_loader_dotsDist,
                defaultDotsDistance
            )
        this.isSingleDir =
            typedArray.getBoolean(
                R.styleable.LinearDotsLoader_loader_isSingleDir,
                defaultIsSingleDir
            )
        this.expandOnSelect =
            typedArray.getBoolean(
                R.styleable.LinearDotsLoader_loader_expandOnSelect,
                defaultExpandOnSelected
            )
        this.toggleOnVisibilityChange =
            typedArray.getBoolean(
                R.styleable.LinearDotsLoader_loader_toggleOnVisibilityChange,
                defaultToggleOnVisibilityChange
            )
        typedArray.recycle()
    }

    override fun initViews() {}

    private fun initCoordinates() {
        diffRadius = selRadius - radius

        dotsXCorArr = FloatArray(this.noOfDots)

        for (i in 0 until noOfDots) {
            dotsXCorArr[i] = (i * dotsDistance + (i * 2 + 1) * radius).toFloat()
        }
    }

    private fun initPaints() {
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

    private fun initShadowPaints() {
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


    // Animation controls
    override fun startAnimation() {
        if (animationTimer != null) return

        animationTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (isSingleDir) {
                        selectedDotPos++
                        if (selectedDotPos > noOfDots) {
                            selectedDotPos = 1
                        }
                    } else {
                        if (isFwdDir) {
                            selectedDotPos++
                            if (selectedDotPos == noOfDots) {
                                isFwdDir = false
                            }
                        } else {
                            selectedDotPos--
                            if (selectedDotPos == 1) {
                                isFwdDir = true
                            }
                        }
                    }

                    (Utils.scanForActivity(context))?.runOnUiThread { invalidate() }
                }
            }, 0, animDur.toLong())
        }
    }
    override fun stopAnimation() {
        if (animationTimer == null) return

        animationTimer?.cancel()
        animationTimer = null
    }
    override fun clearPreviousAnimations() {}

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth: Int
        val calHeight: Int

        if (expandOnSelect) {
            calWidth =
                (2 * this.noOfDots * radius + (this.noOfDots - 1) * dotsDistance + 2 * diffRadius)
            calHeight = 2 * this.selRadius
        } else {
            calHeight = 2 * radius
            calWidth = (2 * this.noOfDots * radius + (this.noOfDots - 1) * dotsDistance)
        }
        setMeasuredDimension(calWidth, calHeight)
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
        drawCircle(canvas)
    }

    // Utility functions
    private fun drawCircle(canvas: Canvas) {
        for (i in 0 until noOfDots) {

            var xCor = dotsXCorArr[i]
            if (expandOnSelect) {
                if (i + 1 == selectedDotPos) {
                    xCor += diffRadius.toFloat()
                } else if (i + 1 > selectedDotPos) {
                    xCor += (2 * diffRadius).toFloat()
                }
            }

            var firstShadowPos: Int
            var secondShadowPos: Int

            if ((isFwdDir && selectedDotPos > 1) || selectedDotPos == noOfDots) {
                firstShadowPos = selectedDotPos - 1
                secondShadowPos = firstShadowPos - 1
            } else {
                firstShadowPos = selectedDotPos + 1
                secondShadowPos = firstShadowPos + 1
            }

            if (i + 1 == selectedDotPos) {

                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) this.selRadius else radius).toFloat(),
                    (if (expandOnSelect) this.selRadius else radius).toFloat(),
                    selectedCirclePaint!!
                )
            } else if (showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) this.selRadius else radius).toFloat(),
                    radius.toFloat(),
                    firstShadowPaint
                )
            } else if (showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) this.selRadius else radius).toFloat(),
                    radius.toFloat(),
                    secondShadowPaint
                )
            } else {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) this.selRadius else radius).toFloat(),
                    radius.toFloat(),
                    defaultCirclePaint!!
                )
            }

        }
    }
}
