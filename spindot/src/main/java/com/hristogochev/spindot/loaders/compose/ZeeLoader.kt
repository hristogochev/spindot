/**
 * Created by hristogochev on 01/02/23.
 */

package com.hristogochev.spindot.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.spindot.loaders.ZeeLoader

@Composable
fun ZeeLoader(
    modifier: Modifier = Modifier,
    dotRadius: Dp? = null,
    firstDotColor: Color? = null,
    secondDotColor: Color? = null,
    distanceMultiplier: Int? = null,
    animDuration: Long? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (ZeeLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }

    AndroidView(modifier = modifier, factory = {
        ZeeLoader(
            context = it,
            dotRadius = dotRadiusPx,
            distanceMultiplier = distanceMultiplier,
            firstDotColor = firstDotColor?.toArgb(),
            secondDotColor = secondDotColor?.toArgb(),
            animDuration = animDuration,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}