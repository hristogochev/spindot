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
import com.hristogochev.spindot.loaders.TrailingLoader

@Composable
fun TrailingLoader(
    modifier: Modifier = Modifier,
    radius: Dp? = null,
    dotRadius: Dp? = null,
    dotColor: Color? = null,
    dotTrailCount: Int? = null,
    animDelay: Long? = null,
    animDuration: Long? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (TrailingLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val radiusPx = with(LocalDensity.current) { radius?.toPx() }
    AndroidView(modifier = modifier, factory = {
        TrailingLoader(
            context = it,
            dotRadius = dotRadiusPx,
            radius = radiusPx,
            dotColor = dotColor?.toArgb(),
            dotTrailCount = dotTrailCount,
            animDuration = animDuration,
            animDelay = animDelay,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}