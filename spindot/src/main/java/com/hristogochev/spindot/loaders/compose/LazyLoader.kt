/**
 * Created by hristogochev on 01/02/23.
 */

package com.hristogochev.spindot.loaders.compose

import android.view.animation.Interpolator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.spindot.loaders.LazyLoader

@Composable
fun LazyLoader(
    modifier: Modifier = Modifier,
    spacing: Dp? = null,
    dotRadius: Dp? = null,
    firstDotColor: Color? = null,
    secondDotColor: Color? = null,
    thirdDotColor: Color? = null,
    animDuration: Long? = null,
    firstDotDelay: Long? = null,
    secondDotDelay: Long? = null,
    interpolator: Interpolator? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (LazyLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        LazyLoader(
            context = it,
            dotRadius = dotRadiusPx,
            spacing = spacingPx,
            firstDotColor = firstDotColor?.toArgb(),
            secondDotColor = secondDotColor?.toArgb(),
            thirdDotColor = thirdDotColor?.toArgb(),
            animDuration = animDuration,
            interpolator = interpolator,
            firstDotDelay = firstDotDelay,
            secondDotDelay = secondDotDelay,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}