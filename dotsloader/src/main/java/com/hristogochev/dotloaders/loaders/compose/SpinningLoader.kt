package com.hristogochev.dotloaders.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.dotloaders.loaders.SpinningLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun SpinningLoader(
    modifier: Modifier = Modifier,
    radius: Dp? = null,
    dotRadius: Dp? = null,
    dotColor: Color? = null,
    animDuration: Long? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (SpinningLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val radiusPx = with(LocalDensity.current) { radius?.toPx() }
    AndroidView(modifier = modifier, factory = {
        SpinningLoader(
            context = it,
            dotRadius = dotRadiusPx,
            radius = radiusPx,
            dotColor = dotColor?.toArgb(),
            animDuration = animDuration,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}