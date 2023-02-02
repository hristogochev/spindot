package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CirclesView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd

class PullInLoader : LinearLayout,AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 30
    private val defaultUseMultipleColors = false
    private val defaultDotsColor = getColorResource(android.R.color.darker_gray)
    private var defaultDotsColorsArray = IntArray(8) { defaultDotsColor }
    private val defaultBigCircleRadius = 90
    private val defaultAnimDuration = 2000
    private val defaultToggleOnVisibilityChange = true

    // Input attributes
    private var dotsRadius = defaultDotsRadius
    private var useMultipleColors = defaultUseMultipleColors
    private var dotsColor = defaultDotsColor
    private var dotsColorsArray = defaultDotsColorsArray
    private var bigCircleRadius = defaultBigCircleRadius
    private var animDuration = defaultAnimDuration

    // Animation attributes
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange
    private var animationStopped = false

    // Base view
    private lateinit var circlesView: CirclesView

    // Single color constructor
    constructor(
        context: Context,
        dotsRadius: Int,
        bigCircleRadius: Int,
        dotsColor: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(
        context
    ) {
        this.dotsRadius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.dotsColor = dotsColor
        this.useMultipleColors = false
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initViews()
    }

    // Multiple colors constructor
    constructor(
        context: Context,
        dotsRadius: Int,
        bigCircleRadius: Int,
        dotsColorsArray: IntArray,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.dotsColorsArray = dotsColorsArray
        this.useMultipleColors = true
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
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


    private fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullInLoader, 0, 0)

        dotsRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.PullInLoader_pullin_dotsRadius,
                defaultDotsRadius
            )

        useMultipleColors =
            typedArray.getBoolean(
                R.styleable.PullInLoader_pullin_useMultipleColors,
                defaultUseMultipleColors
            )

        if (useMultipleColors) {
            val dotsArrayId =
                typedArray.getResourceId(
                    R.styleable.PullInLoader_pullin_colorsArray,
                    0
                )
            dotsColorsArray =
                if (dotsArrayId != 0) calcDotColorsArray(dotsArrayId, defaultDotsColor)
                else defaultDotsColorsArray
        } else {
            dotsColor = typedArray.getColor(
                R.styleable.PullInLoader_pullin_dotsColor,
                defaultDotsColor
            )
        }

        bigCircleRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.PullInLoader_pullin_bigCircleRadius,
                defaultBigCircleRadius
            )

        animDuration =
            typedArray.getInt(R.styleable.PullInLoader_pullin_animDur, defaultAnimDuration)

        toggleOnVisibilityChange =
            typedArray.getBoolean(
                R.styleable.PullInLoader_pullin_toggleOnVisibilityChange,
                defaultToggleOnVisibilityChange
            )

        typedArray.recycle()
    }

    private fun initViews() {
        circlesView = if (useMultipleColors) {
            CirclesView(context, dotsRadius, bigCircleRadius, dotsColorsArray)
        } else {
            CirclesView(context, dotsRadius, bigCircleRadius, dotsColor)
        }

        addView(circlesView)
    }


    // Animation controls
    override fun startAnimation() {
        // Enable animations
        animationStopped = false

        // Create rotate animation
        val rotationAnim = getRotateAnimation()
        rotationAnim.onAnimationEnd {
            // Create scale animation
            val scaleAnimation = getScaleAnimation()
            scaleAnimation.onAnimationEnd {
                if (!animationStopped) startAnimation()
            }
            circlesView.startAnimation(scaleAnimation)
        }

        // Start the rotate animation
        circlesView.startAnimation(rotationAnim)
    }

    override fun stopAnimation() {
        // Disable animations
        animationStopped = true

        // Clear the running animation
        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        circlesView.clearAnimation()
    }


    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = 0
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
    private fun getScaleAnimation(): AnimationSet {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.5f,
            1.0f, 0.5f,
            (circlesView.width / 2).toFloat(),
            (circlesView.height / 2).toFloat()
        ).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }

        val alphaAnimation = AlphaAnimation(1.0f, 0.0f).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }

        val animSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
            repeatCount = 1
            repeatMode = Animation.REVERSE
            duration = if (animDuration > 0) (animDuration / 8).toLong() else 100
            interpolator = AccelerateInterpolator()
        }

        return animSet
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * this.bigCircleRadius + 2 * dotsRadius
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (!toggleOnVisibilityChange) return

        if (visibility != View.VISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }


    // Utility functions
    private fun calcDotColorsArray(arrayId: Int, defaultColor: Int): IntArray {
        val colors = IntArray(8)
        val colorsArray = resources.getIntArray(arrayId)
        for (i in 0..7) {
            colors[i] = if (colorsArray.size > i) colorsArray[i] else defaultColor
        }
        return colors
    }
}