package com.hristogochev.dotloaders.loaders.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hristogochev.dotloaders.loaders.LinearLoader

/**
 * Created by hristogochev on 01/02/23.
 */

@Composable
fun LinearLoader(
    modifier: Modifier = Modifier,
    activeColor: Color? = null,
    inactiveColor: Color? = null,
    dotRadius: Dp? = null,
    dotCount: Int? = null,
    showRunningShadow: Boolean? = null,
    firstShadowColor: Color? = null,
    secondShadowColor: Color? = null,
    spacing: Dp? = null,
    animDuration: Long? = null,
    singleDirection: Boolean? = null,
    expandLeadingDot: Boolean? = null,
    expandedLeadingDotRadius: Dp? = null,
    toggleOnVisibilityChange: Boolean? = null,
    onUpdate: (LinearLoader) -> Unit = {}
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius?.toPx() }
    val expandedLeadingDotRadiusPx = with(LocalDensity.current) { expandedLeadingDotRadius?.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing?.toPx()?.toInt() }
    AndroidView(modifier = modifier, factory = {
        LinearLoader(
            context = it,
            inactiveColor = inactiveColor?.toArgb(),
            activeColor = activeColor?.toArgb(),
            dotRadius = dotRadiusPx,
            animDuration = animDuration,
            showRunningShadow = showRunningShadow,
            firstShadowColor = firstShadowColor?.toArgb(),
            secondShadowColor = secondShadowColor?.toArgb(),
            dotCount = dotCount,
            expandedLeadingDotRadius = expandedLeadingDotRadiusPx,
            spacing = spacingPx,
            singleDirection = singleDirection,
            expandLeadingDot = expandLeadingDot,
            toggleOnVisibilityChange = toggleOnVisibilityChange
        )
    }, update = onUpdate)
}