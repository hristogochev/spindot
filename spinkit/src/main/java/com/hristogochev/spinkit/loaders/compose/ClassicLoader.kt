package com.hristogochev.spinkit.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.spinkit.loaders.ClassicLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun ClassicLoader(
    modifier: Modifier = Modifier,
    activeColor: Color? = null,
    inactiveColor: Color? = null,
    radius: Dp? = null,
    dotRadius: Dp? = null,
    animDuration: Long? = null,
    showRunningShadow: Boolean? = null,
    firstShadowColor: Color? = null,
    secondShadowColor: Color? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (ClassicLoader) -> Unit = {},
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val radiusPx = with(LocalDensity.current) { radius?.toPx() }
    AndroidView(modifier = modifier, factory = {
        ClassicLoader(
            context = it,
            inactiveColor = inactiveColor?.toArgb(),
            activeColor = activeColor?.toArgb(),
            dotRadius = dotRadiusPx,
            radius = radiusPx,
            animDuration = animDuration,
            showRunningShadow = showRunningShadow,
            firstShadowColor = firstShadowColor?.toArgb(),
            secondShadowColor = secondShadowColor?.toArgb(),
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}