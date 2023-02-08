package com.hristogochev.spinkit.loaders.compose

import android.view.animation.Interpolator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.spinkit.loaders.PulsingLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun PulsingLoader(
    modifier: Modifier = Modifier,
    dotRadius: Dp? = null,
    dotColor: Color? = null,
    dotCount: Int? = null,
    spacing: Dp? = null,
    animDelay: Long? = null,
    animDuration: Long? = null,
    interpolator: Interpolator? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (PulsingLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx()}
    val spacingPx = with(LocalDensity.current) { spacing?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        PulsingLoader(
            context = it,
            dotRadius = dotRadiusPx,
            spacing = spacingPx,
            dotColor = dotColor?.toArgb(),
            animDuration = animDuration,
            interpolator = interpolator,
            dotCount = dotCount,
            animDelay = animDelay,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}