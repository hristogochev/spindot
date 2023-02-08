package com.hristogochev.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hristogochev.sample.ui.theme.SpinKitTheme
import com.hristogochev.spinkit.loaders.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpinKitTheme {
                var loader by remember {
                    mutableStateOf(0)
                }
                val coroutineScope = rememberCoroutineScope()
                LaunchedEffect(key1 = Unit, block = {
                    coroutineScope.launch {
                        for (i in 1 until 12) {
                            delay(2000 * 3)
                            loader = i
                        }
                    }
                })
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black), contentAlignment = Alignment.Center
                ) {
                    when (loader) {
                        0 -> BounceLoader(
                            ballRadius = 30.dp,
                            ballColor = Color.Green,
                            showShadow = true,
                            shadowColor = Color.LightGray,
                            animDuration = 1200
                        )
                        1 -> ClassicLoader(
                            activeColor = Color.Red,
                            inactiveColor = Color.White,
                            radius = 40.dp,
                            dotRadius = 12.dp,
                            animDuration = 200,
                            showRunningShadow = true,
                            firstShadowColor = Color.Green,
                            secondShadowColor = Color.Blue
                        )
                        2 -> FidgetLoader(
                            dotRadius = 30.dp,
                            drawOnlyStroke = true,
                            strokeWidth = 8.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            distanceMultiplier = 4,
                            animDuration = 500
                        )
                        3 -> LazyLoader(
                            spacing = 5.dp,
                            dotRadius = 10.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            animDuration = 500,
                            firstDotDelay = 100,
                            secondDotDelay = 200,
                        )
                        4 -> LightsLoader(
                            size = 4,
                            spacing = 5.dp,
                            dotRadius = 12.dp,
                            dotColor = Color.Green
                        )
                        5 -> LinearLoader(
                            activeColor = Color.LightGray,
                            inactiveColor = Color.Gray,
                            dotRadius = 6.dp,
                            dotCount = 8,
                            showRunningShadow = false,
                            spacing = 6.dp,
                            animDuration = 200,
                            singleDirection = false,
                            expandLeadingDot = true,
                            expandedLeadingDotRadius = 10.dp
                        )
                        6 -> PullingLoader(
                            radius = 42.dp,
                            dotRadius = 10.dp,
                            dotColors = listOf(
                                Color.Red,
                                Color.Green,
                                Color.Blue,
                                Color.White,
                                Color.White,
                                Color.White,
                                Color.White,
                                Color.White
                            ),
                            animDuration = 2000,
                        )
                        7 -> PulsingLoader(
                            dotRadius = 12.dp,
                            dotColor = Color.Green,
                            dotCount = 6,
                            spacing = 4.dp,
                            animDelay = 200,
                            animDuration = 1000,
                        )
                        8 -> SlidingLoader(
                            dotRadius = 10.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            spacing = 6.dp,
                            distanceToMove = 12,
                            animDuration = 2000
                        )
                        9 -> SpinningLoader(
                            radius = 40.dp,
                            dotRadius = 10.dp,
                            dotColor = Color.Green,
                            animDuration = 4000
                        )
                        10 -> TrailingLoader(
                            radius = 40.dp,
                            dotRadius = 10.dp,
                            dotColor = Color.Green,
                            dotTrailCount = 5,
                            animDelay = 200,
                            animDuration = 1200
                        )
                        11 -> ZeeLoader(
                            dotRadius = 24.dp,
                            firstDotColor = Color.Green,
                            secondDotColor = Color.Blue,
                            distanceMultiplier = 4,
                            animDuration = 300
                        )
                    }
                }
            }
        }
    }
}

