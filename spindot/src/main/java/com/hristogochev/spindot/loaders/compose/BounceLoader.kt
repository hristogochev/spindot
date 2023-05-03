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
import com.hristogochev.spindot.loaders.BounceLoader

@Composable
fun BounceLoader(
    modifier: Modifier = Modifier,
    ballRadius: Dp?=null,
    ballColor: Color?=null,
    showShadow: Boolean?=null,
    shadowColor: Color?=null,
    animDuration: Long?=null,
    toggleOnVisibilityChange: Boolean?=null,
    onUpdate: (BounceLoader) -> Unit = {}
) {
    val ballRadiusPx = with(LocalDensity.current) { ballRadius?.toPx() }
    AndroidView(modifier = modifier, factory = {
        BounceLoader(
            context = it,
            ballRadius = ballRadiusPx,
            ballColor = ballColor?.toArgb(),
            shadowColor = shadowColor?.toArgb(),
            showShadow = showShadow,
            animDuration = animDuration,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}