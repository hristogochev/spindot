package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd

/**
 * Created by suneet on 12/13/17.
 * Modified by hristogochev on 02/02/23.
 */
class SlidingLoader : LinearLayout, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 30
    private val defaultDotsDist = 15
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultThirdDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration = 500
    private val defaultDistanceToMove = 12
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var dotsDist = defaultDotsDist
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var thirdDotColor = defaultThirdDotColor
    private var animDuration = defaultAnimDuration
        set(value) {
            field = value
            firstDelayDuration = value / 10
            secondDelayDuration = value / 5
        }
    private var distanceToMove = defaultDistanceToMove
        set(value) {
            field = value
            invalidate()
        }
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Animation attributes
    private val anticipateOvershootInterpolator = AnticipateOvershootInterpolator()
    private var firstDelayDuration = defaultAnimDuration / 10
    private var secondDelayDuration = defaultAnimDuration / 5
    private var animationStopped = false

    // Views
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var thirdCircle: CircleView

    // Custom constructors
    constructor(
        context: Context,
        dotsRadius: Int,
        dotsDist: Int,
        firstDotColor: Int,
        secondDotColor: Int,
        thirdDotColor: Int,
        animDuration: Int,
        distanceToMove: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.dotsDist = dotsDist
        this.firstDotColor = firstDotColor
        this.secondDotColor = secondDotColor
        this.thirdDotColor = thirdDotColor
        this.animDuration = animDuration
        this.distanceToMove = distanceToMove
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

    // Initialization functions
    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingLoader, 0, 0)

        this.dotsRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.SlidingLoader_slidingloader_dotsRadius,
                defaultDotsRadius
            )
        this.dotsDist =
            typedArray.getDimensionPixelSize(
                R.styleable.SlidingLoader_slidingloader_dotsDist,
                defaultDotsDist
            )
        this.firstDotColor = typedArray.getColor(
            R.styleable.SlidingLoader_slidingloader_firstDotColor,
            defaultFirstDotColor
        )
        this.secondDotColor = typedArray.getColor(
            R.styleable.SlidingLoader_slidingloader_secondDotColor,
            defaultSecondDotColor
        )
        this.thirdDotColor = typedArray.getColor(
            R.styleable.SlidingLoader_slidingloader_thirdDotColor,
            defaultThirdDotColor
        )
        this.animDuration =
            typedArray.getInt(R.styleable.SlidingLoader_slidingloader_animDur, defaultAnimDuration)
        this.distanceToMove =
            typedArray.getInteger(
                R.styleable.SlidingLoader_slidingloader_distanceToMove,
                defaultDistanceToMove
            )
        this.toggleOnVisibilityChange =
            typedArray.getBoolean(
                R.styleable.SlidingLoader_slidingloader_toggleOnVisibilityChange,
                defaultToggleOnVisibilityChange
            )

        typedArray.recycle()
    }

    private fun initViews() {
        firstCircle = CircleView(context, dotsRadius, firstDotColor)
        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        thirdCircle = CircleView(context, dotsRadius, thirdDotColor)

        val paramsFirstCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsFirstCircle.leftMargin = (2 * dotsRadius)

        val paramsSecondCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsSecondCircle.leftMargin = dotsDist

        val paramsThirdCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsThirdCircle.leftMargin = dotsDist
        paramsThirdCircle.rightMargin = (2 * dotsRadius)

        addView(firstCircle, paramsFirstCircle)
        addView(secondCircle, paramsSecondCircle)
        addView(thirdCircle, paramsThirdCircle)
    }


    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        startAnimationLoop(true)
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
    private fun startAnimationLoop(isForwardDir: Boolean) {
        val trans1Anim = getTranslateAnim(isForwardDir)
        if (isForwardDir) thirdCircle.startAnimation(trans1Anim) else firstCircle.startAnimation(
            trans1Anim
        )

        val trans2Anim = getTranslateAnim(isForwardDir)

        Handler(Looper.getMainLooper()).postDelayed({
            secondCircle.startAnimation(trans2Anim)
        }, firstDelayDuration.toLong())

        val trans3Anim = getTranslateAnim(isForwardDir)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isForwardDir) firstCircle.startAnimation(trans3Anim) else thirdCircle.startAnimation(
                trans3Anim
            )
        }, secondDelayDuration.toLong())

        trans3Anim.onAnimationEnd {
            if (!animationStopped) {
                startAnimationLoop(!isForwardDir)
            }
        }
    }


    // Animations
    private fun getTranslateAnim(isForwardDir: Boolean): TranslateAnimation {
        return TranslateAnimation(
            if (isForwardDir) 0f else (distanceToMove * dotsRadius).toFloat(),
            if (isForwardDir) (distanceToMove * dotsRadius).toFloat() else 0f,
            0f, 0f
        ).apply {
            duration = animDuration.toLong()
            fillAfter = true
            interpolator = anticipateOvershootInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (10 * dotsRadius) + (distanceToMove * dotsRadius) + (2 * dotsDist)
        val calHeight = 2 * dotsRadius

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