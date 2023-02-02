package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.onAnimationEnd

/**
 * Created by agrawalsuneet on 4/13/19.
 * Modified by hristogochev on 02/02/23.
 */

class BounceLoader : LinearLayout, AnimationContract {
    companion object {
        private const val STATE_GOING_DOWN: Int = 0
        private const val STATE_SQUEEZING: Int = 1
        private const val STATE_RESIZING: Int = 2
        private const val STATE_COMING_UP: Int = 3
    }
    // Default input attributes
    private val defaultBallRadius = 60
    private val defaultBallColor = getColorResource(android.R.color.holo_red_dark)
    private val defaultShadowColor = getColorResource(android.R.color.black)
    private val defaultShowShadow = true
    private val defaultAnimDuration = 1500
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var ballRadius = defaultBallRadius
    private var ballColor = defaultBallColor
    private var shadowColor = defaultShadowColor
    private var showShadow = defaultShowShadow
    private var animDuration = defaultAnimDuration
        set(value) {
            field = if (value <= 0) 1000 else value
        }
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Animation attributes
    private var calWidth: Int = 0
    private var calHeight: Int = 0
    private var state: Int = STATE_GOING_DOWN
    private var animationStopped = false

    // Views
    private var relativeLayout: RelativeLayout? = null
    private var ballCircleView: CircleView? = null
    private var ballShadowView: CircleView? = null


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

    // Custom constructors
    constructor(
        context: Context?,
        ballRadius: Int,
        ballColor: Int,
        showShadow: Boolean,
        shadowColor: Int = 0,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.ballRadius = ballRadius
        this.ballColor = ballColor
        this.showShadow = showShadow
        this.shadowColor = shadowColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initViews()
    }

    fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BounceLoader, 0, 0)

        this.ballRadius = typedArray.getDimensionPixelSize(
            R.styleable.BounceLoader_bounce_ballRadius,
            defaultBallRadius
        )
        this.ballColor = typedArray.getColor(
            R.styleable.BounceLoader_bounce_ballColor,
            defaultBallColor
        )

        this.shadowColor = typedArray.getColor(
            R.styleable.BounceLoader_bounce_shadowColor,
            defaultShadowColor
        )
        this.showShadow =
            typedArray.getBoolean(R.styleable.BounceLoader_bounce_showShadow, defaultShowShadow)
        this.animDuration =
            typedArray.getInt(R.styleable.BounceLoader_bounce_animDuration, defaultAnimDuration)
        this.toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.BounceLoader_bounce_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }


    private fun initViews() {
        if (calWidth == 0 || calHeight == 0) {
            calWidth = 5 * ballRadius
            calHeight = 8 * ballRadius
        }

        relativeLayout = RelativeLayout(context)

        if (showShadow) {
            ballShadowView = CircleView(
                context = context,
                circleRadius = ballRadius,
                circleColor = shadowColor,
                isAntiAlias = false
            )

            val shadowParam = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            }

            relativeLayout?.addView(ballShadowView, shadowParam)
        }

        ballCircleView = CircleView(
            context = context,
            circleRadius = ballRadius,
            circleColor = ballColor
        )

        val ballParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        }

        relativeLayout?.addView(ballCircleView, ballParam)

        val relParam = RelativeLayout.LayoutParams(calWidth, calHeight)
        this.addView(relativeLayout, relParam)
    }

    // Animation controls
    override fun startAnimation() {
        animationStopped = false

        clearPreviousAnimations()

        val ballAnim = getBallAnimation().apply {
            this.onAnimationEnd {
                state = (state + 1) % 4
                if (!animationStopped) startAnimation()

            }
        }

        if (showShadow) {
            if (state == STATE_SQUEEZING || state == STATE_RESIZING) {
                ballShadowView?.clearAnimation()
                ballShadowView?.visibility = View.GONE
            } else {
                ballShadowView?.visibility = View.VISIBLE
                val shadowAnim = getShadowAnimation()
                ballShadowView?.startAnimation(shadowAnim)
            }
        }

        ballCircleView?.startAnimation(ballAnim)
    }

    override fun stopAnimation() {
        animationStopped = true

        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        ballShadowView?.clearAnimation()
        ballCircleView?.clearAnimation()
    }

    // Animations
    private fun getShadowAnimation(): AnimationSet {

        val transAnim: Animation
        val scaleAnim: Animation
        val alphaAnim: AlphaAnimation

        val set = AnimationSet(true)

        when (state) {
            STATE_COMING_UP -> {
                transAnim = TranslateAnimation(
                    0.0f, (-4 * ballRadius).toFloat(),
                    0.0f, (-3 * ballRadius).toFloat()
                )

                scaleAnim = ScaleAnimation(
                    0.9f, 0.5f, 0.9f, 0.5f,
                    ballRadius.toFloat(), ballRadius.toFloat()
                )

                alphaAnim = AlphaAnimation(0.6f, 0.2f)

                set.interpolator = DecelerateInterpolator()
            }
            else -> {
                transAnim = TranslateAnimation(
                    (-4 * ballRadius).toFloat(), 0.0f,
                    (-3 * ballRadius).toFloat(), 0.0f
                )

                scaleAnim = ScaleAnimation(
                    0.5f, 0.9f, 0.5f, 0.9f,
                    ballRadius.toFloat(), ballRadius.toFloat()
                )

                alphaAnim = AlphaAnimation(0.2f, 0.6f)

                set.interpolator = AccelerateInterpolator()
            }
        }

        set.addAnimation(transAnim)
        set.addAnimation(scaleAnim)
        set.addAnimation(alphaAnim)

        return set.apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = 0
        }
    }

    private fun getBallAnimation(): Animation {
        return when (state) {
            STATE_GOING_DOWN -> {
                TranslateAnimation(
                    0.0f, 0.0f,
                    (-6 * ballRadius).toFloat(), 0.0f
                ).apply {
                    duration = animDuration.toLong()
                    interpolator = AccelerateInterpolator()
                }
            }
            STATE_SQUEEZING -> {
                ScaleAnimation(
                    1.0f, 1.0f, 1.0f, 0.85f, ballRadius.toFloat(), (2 * ballRadius).toFloat()
                ).apply {
                    duration = (animDuration / 20).toLong()
                    interpolator = AccelerateInterpolator()
                }
            }
            STATE_RESIZING -> {
                ScaleAnimation(
                    1.0f, 1.0f, 0.85f, 1.0f, ballRadius.toFloat(), (2 * ballRadius).toFloat()
                ).apply {
                    duration = (animDuration / 20).toLong()
                    interpolator = DecelerateInterpolator()
                }
            }
            else -> {
                TranslateAnimation(
                    0.0f, 0.0f,
                    0.0f, (-6 * ballRadius).toFloat()
                ).apply {
                    duration = animDuration.toLong()
                    interpolator = DecelerateInterpolator()
                }
            }
        }.apply {
            fillAfter = true
            repeatCount = 0
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidth == 0 || calHeight == 0) {
            calWidth = 5 * ballRadius
            calHeight = 8 * ballRadius
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
}