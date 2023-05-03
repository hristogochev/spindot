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
import com.hristogochev.spindot.loaders.LightsLoader

@Composable
fun LightsLoader(
    modifier: Modifier = Modifier,
    size: Int? = null,
    spacing: Dp? = null,
    dotRadius: Dp? = null,
    dotColor: Color? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (LightsLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        LightsLoader(
            context = it,
            size = size,
            dotRadius = dotRadiusPx,
            spacing = spacingPx,
            dotColor = dotColor?.toArgb(),
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}