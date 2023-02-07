package com.hristogochev.dotloaders.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.dotloaders.loaders.FidgetLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun FidgetLoader(
    modifier: Modifier = Modifier,
    dotRadius: Dp? = null,
    drawOnlyStroke: Boolean? = null,
    strokeWidth: Dp? = null,
    firstDotColor: Color? = null,
    secondDotColor: Color? = null,
    thirdDotColor: Color? = null,
    distanceMultiplier: Int? = null,
    animDuration: Long? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (FidgetLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        FidgetLoader(
            context = it,
            dotRadius = dotRadiusPx,
            distanceMultiplier = distanceMultiplier,
            drawOnlyStroke = drawOnlyStroke,
            strokeWidth = strokeWidthPx,
            firstDotColor = firstDotColor?.toArgb(),
            secondDotColor = secondDotColor?.toArgb(),
            thirdDotColor = thirdDotColor?.toArgb(),
            animDuration = animDuration,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}