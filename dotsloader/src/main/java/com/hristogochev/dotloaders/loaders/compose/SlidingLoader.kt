package com.hristogochev.dotloaders.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.dotloaders.loaders.SlidingLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun SlidingLoader(
    modifier: Modifier = Modifier,
    dotRadius: Dp? = null,
    firstDotColor: Color? = null,
    secondDotColor: Color? = null,
    thirdDotColor: Color? = null,
    spacing: Dp? = null,
    distanceToMove: Int? = null,
    animDuration: Long? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (SlidingLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        SlidingLoader(
            context = it,
            dotRadius = dotRadiusPx,
            spacing = spacingPx,
            firstDotColor = firstDotColor?.toArgb(),
            secondDotColor = secondDotColor?.toArgb(),
            thirdDotColor = thirdDotColor?.toArgb(),
            animDuration = animDuration,
            distanceToMove = distanceToMove,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}