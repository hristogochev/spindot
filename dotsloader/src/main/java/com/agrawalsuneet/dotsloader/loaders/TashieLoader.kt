package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
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
 * Created by suneet on 10/10/17.
 * Modified by hristogochev on 02/02/23.
 */
class TashieLoader : LinearLayout, AnimationContract {

    // Default input attributes
    private val defaultDotsRadius = 30
    private val defaultDotsDist = 15
    private val defaultDotsColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration = 500
    private val defaultInterpolator = LinearInterpolator()
    private val defaultNoOfDots = 8
    private val defaultAnimDelay = 100
    private val defaultToggleOnVisibilityChange = true


    // Settable attributes
    private var dotsRadius = defaultDotsRadius
    private var dotsDist = defaultDotsDist
    private var dotsColor = defaultDotsColor
    private var animDuration = defaultAnimDuration
    private var loaderInterpolator: Interpolator = defaultInterpolator
    private var noOfDots = defaultNoOfDots
    private var animDelay = defaultAnimDelay
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange


    // Animation attributes
    private var isDotsExpanding = true
    private var animationStopped = false

    // Views
    private lateinit var dotsArray: List<CircleView>

    // Custom constructors
    constructor(
        context: Context,
        dotsRadius: Int,
        dotsDist: Int,
        dotsColor: Int,
        animDuration: Int,
        interpolator: Interpolator,
        noOfDots: Int,
        animDelay: Int,
        toggleOnVisibilityChange: Boolean,
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.dotsDist = dotsDist
        this.dotsColor = dotsColor
        this.animDuration = animDuration
        this.loaderInterpolator = interpolator
        this.noOfDots = noOfDots
        this.animDelay = animDelay
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TashieLoader, 0, 0)

        this.dotsRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.TashieLoader_tashieloader_dotsRadius,
                defaultDotsRadius
            )
        this.dotsDist =
            typedArray.getDimensionPixelSize(
                R.styleable.TashieLoader_tashieloader_dotsDist,
                defaultDotsDist
            )
        this.dotsColor = typedArray.getColor(
            R.styleable.TashieLoader_tashieloader_dotsColor,
            defaultDotsColor
        )

        this.animDuration =
            typedArray.getInt(R.styleable.TashieLoader_tashieloader_animDur, defaultAnimDuration)

        this.loaderInterpolator = AnimationUtils.loadInterpolator(
            context,
            typedArray.getResourceId(
                R.styleable.TashieLoader_tashieloader_interpolator,
                android.R.anim.linear_interpolator
            )
        )

        this.noOfDots =
            typedArray.getInt(R.styleable.TashieLoader_tashieloader_noOfDots, defaultNoOfDots)
        this.animDelay =
            typedArray.getInt(R.styleable.TashieLoader_tashieloader_animDelay, defaultAnimDelay)
        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.TashieLoader_tashieloader_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initViews() {
        setVerticalGravity(Gravity.BOTTOM)

        dotsArray = (0 until noOfDots).map {
            CircleView(context, dotsRadius, dotsColor)
        }

        dotsArray.forEachIndexed { iCount, circle ->
            val params = LayoutParams(2 * dotsRadius, 2 * dotsRadius)
            if (iCount != noOfDots - 1) {
                params.rightMargin = dotsDist
            }
            addView(circle, params)
        }
    }


    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        for (iCount in 0 until noOfDots) {
            val anim = getScaleAnimation(isDotsExpanding, iCount)
            dotsArray[iCount].startAnimation(anim)

            if (iCount == noOfDots - 1) {
                anim.onAnimationEnd {
                    if (!animationStopped) startAnimation()
                }
            } else {
                anim.onAnimationEnd {
                    dotsArray[iCount].visibility =
                        if (!isDotsExpanding) View.VISIBLE else View.INVISIBLE
                }
            }
        }
        isDotsExpanding = !isDotsExpanding
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        dotsArray.forEach {
            it.clearAnimation()
        }
    }

    // Animations
    private fun getScaleAnimation(isExpanding: Boolean, delay: Int): AnimationSet {
        val scaleAnim: ScaleAnimation = if (isExpanding) {
            ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        } else {
            ScaleAnimation(
                1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        }.apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = 0
            startOffset = (animDelay * delay).toLong()
        }

        return AnimationSet(true).apply {
            addAnimation(scaleAnim)
            interpolator = loaderInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calHeight = 2 * dotsRadius
        val calWidth = ((2 * noOfDots * dotsRadius) + ((noOfDots - 1) * dotsDist))

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
