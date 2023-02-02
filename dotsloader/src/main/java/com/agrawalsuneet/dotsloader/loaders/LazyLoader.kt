package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd


/**
 * Created by ballu on 13/08/17.
 * Modified by hristogochev on 02/02/23.
 */
class LazyLoader : LinearLayout, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 30
    private val defaultDotsDist = 15
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultThirdDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration = 500
    private val defaultInterpolator = LinearInterpolator()
    private val defaultFirstDelayDuration = 100
    private val defaultSecondDelayDuration = 200
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var dotsDist = defaultDotsDist
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var thirdDotColor = defaultThirdDotColor
    private var animDuration = defaultAnimDuration
    private var interpolator: Interpolator = defaultInterpolator
    private var firstDelayDuration = defaultFirstDelayDuration
    private var secondDelayDuration = defaultSecondDelayDuration
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange


    // Views
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var thirdCircle: CircleView

    // Animation attributes
    private var animationStopped = false

    // Custom constructors
    constructor(
        context: Context,
        dotsRadius: Int,
        dotsDist: Int,
        firstDotColor: Int,
        secondDotColor: Int,
        thirdDotColor: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.dotsDist = dotsDist
        this.firstDotColor = firstDotColor
        this.secondDotColor = secondDotColor
        this.thirdDotColor = thirdDotColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initViews()
    }

    // Default constructors
    constructor(context: Context?) : super(context) {
        initViews()
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initViews()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initViews()
    }

    // Initialization functions
    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LazyLoader, 0, 0)

        this.dotsRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.LazyLoader_lazyloader_dotsRadius,
                defaultDotsRadius
            )
        this.dotsDist =
            typedArray.getDimensionPixelSize(
                R.styleable.LazyLoader_lazyloader_dotsDist,
                defaultDotsDist
            )
        this.firstDotColor = typedArray.getColor(
            R.styleable.LazyLoader_lazyloader_firstDotColor,
            defaultFirstDotColor
        )
        this.secondDotColor = typedArray.getColor(
            R.styleable.LazyLoader_lazyloader_secondDotColor,
            defaultSecondDotColor
        )
        this.thirdDotColor = typedArray.getColor(
            R.styleable.LazyLoader_lazyloader_thirdDotColor,
            defaultThirdDotColor
        )
        this.animDuration =
            typedArray.getInt(R.styleable.LazyLoader_lazyloader_animDur, defaultAnimDuration)
        this.interpolator = AnimationUtils.loadInterpolator(
            context,
            typedArray.getResourceId(
                R.styleable.LazyLoader_lazyloader_interpolator,
                android.R.anim.linear_interpolator
            )
        )
        this.firstDelayDuration =
            typedArray.getInt(
                R.styleable.LazyLoader_lazyloader_firstDelayDur,
                defaultFirstDelayDuration
            )
        this.secondDelayDuration =
            typedArray.getInt(
                R.styleable.LazyLoader_lazyloader_secondDelayDur,
                defaultSecondDelayDuration
            )
        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.LazyLoader_lazyloader_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initViews() {
        firstCircle = CircleView(context, dotsRadius, firstDotColor)
        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        thirdCircle = CircleView(context, dotsRadius, thirdDotColor)

        val params = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
            .apply { leftMargin = dotsDist }

        setVerticalGravity(Gravity.BOTTOM)

        addView(firstCircle)
        addView(secondCircle, params)
        addView(thirdCircle, params)
    }


    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        clearPreviousAnimations()

        val trans1Anim = getTranslateAnim()
        firstCircle.startAnimation(trans1Anim)

        val trans2Anim = getTranslateAnim()
        Handler(Looper.getMainLooper()).postDelayed({
            secondCircle.startAnimation(trans2Anim)
        }, firstDelayDuration.toLong())

        val trans3Anim = getTranslateAnim()
        Handler(Looper.getMainLooper()).postDelayed({
            thirdCircle.startAnimation(trans3Anim)
        }, secondDelayDuration.toLong())

        trans3Anim.onAnimationEnd {
            if (!animationStopped) startAnimation()
        }
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        firstCircle.clearAnimation()
        secondCircle.clearAnimation()
        thirdCircle.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(): TranslateAnimation {
        return TranslateAnimation(0f, 0f, 0f, (-(4 * dotsRadius).toFloat())).apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = 1
            repeatMode = Animation.REVERSE
            interpolator = interpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (6 * dotsRadius) + (2 * dotsDist)
        val calHeight = 6 * dotsRadius

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
}