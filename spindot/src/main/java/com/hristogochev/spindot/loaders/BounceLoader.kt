/**
 * Created by agrawalsuneet on 4/13/19.
 *
 * Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.RelativeLayout
import com.hristogochev.spindot.R
import com.hristogochev.spindot.animation.AnimationLayout
import com.hristogochev.spindot.basicviews.DotView
import com.hristogochev.spindot.utils.getColorResource
import com.hristogochev.spindot.utils.onAnimationEnd

class BounceLoader : com.hristogochev.spindot.animation.AnimationLayout {
    companion object {
        private const val STATE_GOING_DOWN: Int = 0
        private const val STATE_SQUEEZING: Int = 1
        private const val STATE_RESIZING: Int = 2
        private const val STATE_COMING_UP: Int = 3
    }

    // Default input attributes
    private val defaultBallRadius = 60f
    private val defaultBallColor = getColorResource(android.R.color.holo_red_dark)
    private val defaultShadowColor = getColorResource(android.R.color.black)
    private val defaultShowShadow = true
    private val defaultAnimDuration: Long = 1500

    // Settable attributes
    private var ballRadius = defaultBallRadius
    private var ballColor = defaultBallColor
    private var shadowColor = defaultShadowColor
    private var showShadow = defaultShowShadow
    private var animDuration = defaultAnimDuration
        set(value) {
            field = if (value <= 0) 1000 else value
        }

    // Animation attributes
    private var calWidth: Int = 0
    private var calHeight: Int = 0
    private var state: Int = STATE_GOING_DOWN
    private val accelerateInterpolator = AccelerateInterpolator()
    private val decelerateInterpolator = DecelerateInterpolator()

    // Views
    private var relativeLayout: RelativeLayout? = null
    private lateinit var ballDotView: com.hristogochev.spindot.basicviews.DotView
    private lateinit var ballShadowView: com.hristogochev.spindot.basicviews.DotView

    // Custom constructors
    constructor(
        context: Context,
        ballRadius: Float? = null,
        ballColor: Int? = null,
        shadowColor: Int? = null,
        showShadow: Boolean? = null,
        animDuration: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.ballRadius = ballRadius ?: defaultBallRadius
        this.ballColor = ballColor ?: defaultBallColor
        this.shadowColor = shadowColor ?: defaultShadowColor
        this.showShadow = showShadow ?: defaultShowShadow
        this.animDuration = animDuration ?: defaultAnimDuration
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

    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BounceLoader, 0, 0)

        try {
            with(typedArray) {
                ballRadius = getDimension(
                    R.styleable.BounceLoader_bounce_ballRadius,
                    defaultBallRadius
                )
                ballColor = getColor(
                    R.styleable.BounceLoader_bounce_ballColor,
                    defaultBallColor
                )
                shadowColor = getColor(
                    R.styleable.BounceLoader_bounce_shadowColor,
                    defaultShadowColor
                )
                showShadow =
                    getBoolean(R.styleable.BounceLoader_bounce_showShadow, defaultShowShadow)
                animDuration =
                    getInt(
                        R.styleable.BounceLoader_bounce_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }


    override fun initViews() {
        if (calWidth == 0 || calHeight == 0) {
            calWidth = 5 * ballRadius.toInt()
            calHeight = 8 * ballRadius.toInt()
        }

        relativeLayout = RelativeLayout(context)

        if (showShadow) {
            ballShadowView = com.hristogochev.spindot.basicviews.DotView(
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

        ballDotView = com.hristogochev.spindot.basicviews.DotView(
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

        relativeLayout?.addView(ballDotView, ballParam)

        val relParam = RelativeLayout.LayoutParams(calWidth, calHeight)
        this.addView(relativeLayout, relParam)
    }

    // Animation controls
    override fun playAnimationLoop() {
        if (animationStopped) return

        val ballAnim = getBallAnimation().apply {
            this.onAnimationEnd {
                state = (state + 1) % 4

                playAnimationLoop()
            }
        }

        if (showShadow) {
            if (state == STATE_SQUEEZING || state == STATE_RESIZING) {
                ballShadowView.clearAnimation()
                ballShadowView.visibility = View.GONE
            } else {
                ballShadowView.visibility = View.VISIBLE
                val shadowAnim = getShadowAnimation()
                ballShadowView.startAnimation(shadowAnim)
            }
        }

        ballDotView.startAnimation(ballAnim)
    }

    override fun clearPreviousAnimations() {
        if (showShadow) ballShadowView.clearAnimation()
        ballDotView.clearAnimation()
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
                    0.0f, (-4 * ballRadius),
                    0.0f, (-3 * ballRadius)
                )

                scaleAnim = ScaleAnimation(
                    0.9f, 0.5f, 0.9f, 0.5f,
                    ballRadius, ballRadius
                )

                alphaAnim = AlphaAnimation(0.6f, 0.2f)

                set.interpolator = decelerateInterpolator
            }
            else -> {
                transAnim = TranslateAnimation(
                    (-4 * ballRadius), 0.0f,
                    (-3 * ballRadius), 0.0f
                )

                scaleAnim = ScaleAnimation(
                    0.5f, 0.9f, 0.5f, 0.9f,
                    ballRadius, ballRadius
                )

                alphaAnim = AlphaAnimation(0.2f, 0.6f)

                set.interpolator = accelerateInterpolator
            }
        }

        set.addAnimation(transAnim)
        set.addAnimation(scaleAnim)
        set.addAnimation(alphaAnim)

        return set.apply {
            duration = animDuration
            fillAfter = true
            repeatCount = 0
        }
    }

    private fun getBallAnimation(): Animation {
        return when (state) {
            STATE_GOING_DOWN -> {
                TranslateAnimation(
                    0.0f, 0.0f,
                    (-6 * ballRadius), 0.0f
                ).apply {
                    duration = animDuration
                    interpolator = accelerateInterpolator
                }
            }
            STATE_SQUEEZING -> {
                ScaleAnimation(
                    1.0f, 1.0f, 1.0f, 0.85f, ballRadius, (2 * ballRadius)
                ).apply {
                    duration = (animDuration / 20)
                    interpolator = accelerateInterpolator
                }
            }
            STATE_RESIZING -> {
                ScaleAnimation(
                    1.0f, 1.0f, 0.85f, 1.0f, ballRadius, (2 * ballRadius)
                ).apply {
                    duration = (animDuration / 20)
                    interpolator = decelerateInterpolator
                }
            }
            else -> {
                TranslateAnimation(
                    0.0f, 0.0f,
                    0.0f, (-6 * ballRadius)
                ).apply {
                    duration = animDuration
                    interpolator = decelerateInterpolator
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
            calWidth = (5 * ballRadius).toInt()
            calHeight = (8 * ballRadius).toInt()
        }

        setMeasuredDimension(calWidth, calHeight)
    }
}