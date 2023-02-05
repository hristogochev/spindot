package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.*
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotView
import com.hristogochev.dotloaders.utils.getColorResource
import com.hristogochev.dotloaders.utils.onAnimationEnd


/**
 * Created by ballu on 13/08/17.
 *
 * Modified by hristogochev on 02/02/23.
 */
class JumpLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultSpacing = 15
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultThirdDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration: Long = 500
    private val defaultInterpolator = LinearInterpolator()
    private val defaultFirstDotDelay: Long = 100
    private val defaultSecondDotDelay: Long = 200

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var spacing = defaultSpacing
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var thirdDotColor = defaultThirdDotColor
    private var animDuration = defaultAnimDuration
    private var animInterpolator: Interpolator = defaultInterpolator
    private var firstDotDelay = defaultFirstDotDelay
    private var secondDotDelay = defaultSecondDotDelay

    // Views
    private lateinit var firstDot: DotView
    private lateinit var secondDot: DotView
    private lateinit var thirdDot: DotView


    // General
    private var dotsDiameter = (2 * dotRadius).toInt()

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        spacing: Int? = null,
        firstDotColor: Int? = null,
        secondDotColor: Int? = null,
        thirdDotColor: Int? = null,
        animDuration: Long? = null,
        interpolator: Interpolator? = null,
        firstDotDelay: Long? = null,
        secondDotDelay: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.spacing = spacing ?: defaultSpacing
        this.firstDotColor = firstDotColor ?: defaultFirstDotColor
        this.secondDotColor = secondDotColor ?: defaultSecondDotColor
        this.thirdDotColor = thirdDotColor ?: defaultThirdDotColor
        this.animDuration = animDuration ?: defaultAnimDuration
        this.animInterpolator = interpolator ?: defaultInterpolator
        this.firstDotDelay = firstDotDelay ?: defaultFirstDotDelay
        this.secondDotDelay = secondDotDelay ?: defaultSecondDotDelay
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initViews()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initViews()
    }

    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.JumpLoader, 0, 0)

        this.dotRadius =
            typedArray.getDimension(
                R.styleable.JumpLoader_jump_dotRadius,
                defaultDotRadius
            )
        this.spacing =
            typedArray.getDimensionPixelSize(
                R.styleable.JumpLoader_jump_spacing,
                defaultSpacing
            )
        this.firstDotColor = typedArray.getColor(
            R.styleable.JumpLoader_jump_firstDotColor,
            defaultFirstDotColor
        )
        this.secondDotColor = typedArray.getColor(
            R.styleable.JumpLoader_jump_secondDotColor,
            defaultSecondDotColor
        )
        this.thirdDotColor = typedArray.getColor(
            R.styleable.JumpLoader_jump_thirdDotColor,
            defaultThirdDotColor
        )
        this.animDuration =
            typedArray.getInt(
                R.styleable.JumpLoader_jump_animDuration,
                defaultAnimDuration.toInt()
            ).toLong()
        this.animInterpolator = AnimationUtils.loadInterpolator(
            context,
            typedArray.getResourceId(
                R.styleable.JumpLoader_jump_interpolator,
                android.R.anim.linear_interpolator
            )
        )
        this.firstDotDelay =
            typedArray.getInt(
                R.styleable.JumpLoader_jump_firstDotDelay,
                defaultFirstDotDelay.toInt()
            ).toLong()
        this.secondDotDelay =
            typedArray.getInt(
                R.styleable.JumpLoader_jump_secondDotDelay,
                defaultSecondDotDelay.toInt()
            ).toLong()

        typedArray.recycle()
    }

    override fun initViews() {
        dotsDiameter = (2 * dotRadius).toInt()

        firstDot = DotView(context, dotRadius, firstDotColor)
        secondDot = DotView(context, dotRadius, secondDotColor)
        thirdDot = DotView(context, dotRadius, thirdDotColor)

        val params = LayoutParams(dotsDiameter, dotsDiameter)
            .apply { leftMargin = spacing }

        setVerticalGravity(Gravity.BOTTOM)

        addView(firstDot)
        addView(secondDot, params)
        addView(thirdDot, params)
    }


    // Animation controls
    override fun playAnimationLoop() {
        val trans1Anim = getTranslateAnim()
        firstDot.startAnimation(trans1Anim)

        val trans2Anim = getTranslateAnim()
        Handler(Looper.getMainLooper()).postDelayed({
            secondDot.startAnimation(trans2Anim)
        }, firstDotDelay)

        val trans3Anim = getTranslateAnim()
        Handler(Looper.getMainLooper()).postDelayed({
            thirdDot.startAnimation(trans3Anim)
        }, secondDotDelay)

        trans3Anim.onAnimationEnd {
            if (!animationStopped) playAnimationLoop()
        }
    }

    override fun clearPreviousAnimations() {
        firstDot.clearAnimation()
        secondDot.clearAnimation()
        thirdDot.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(): TranslateAnimation {
        return TranslateAnimation(0f, 0f, 0f, (-(4 * dotRadius))).apply {
            duration = animDuration
            fillAfter = true
            repeatCount = 1
            repeatMode = Animation.REVERSE
            interpolator = animInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (3 * dotsDiameter) + (2 * spacing)
        val calHeight = 3 * dotsDiameter

        setMeasuredDimension(calWidth, calHeight)
    }
}