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
import com.hristogochev.sample.ui.theme.SpinDotTheme
import com.hristogochev.spindot.loaders.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpinDotTheme {
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

                        1 -> FidgetLoader(
                            dotRadius = 30.dp,
                            drawOnlyStroke = true,
                            strokeWidth = 8.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            distanceMultiplier = 4,
                            animDuration = 500
                        )

                        2 -> LazyLoader(
                            spacing = 5.dp,
                            dotRadius = 10.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            animDuration = 500,
                            firstDotDelay = 100,
                            secondDotDelay = 200,
                        )

                        3 -> LightsLoader(
                            size = 4,
                            spacing = 5.dp,
                            dotRadius = 12.dp,
                            dotColor = Color.Green
                        )

                        4 -> PullingLoader(
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

                        5 -> PulsingLoader(
                            dotRadius = 12.dp,
                            dotColor = Color.Green,
                            dotCount = 6,
                            spacing = 4.dp,
                            animDelay = 200,
                            animDuration = 1000,
                        )

                        6 -> SlidingLoader(
                            dotRadius = 10.dp,
                            firstDotColor = Color.Red,
                            secondDotColor = Color.Green,
                            thirdDotColor = Color.Blue,
                            spacing = 6.dp,
                            distanceToMove = 12,
                            animDuration = 2000
                        )

                        7 -> SpinningLoader(
                            radius = 40.dp,
                            dotRadius = 10.dp,
                            dotColor = Color.Green,
                            animDuration = 4000
                        )

                        8 -> TrailingLoader(
                            radius = 40.dp,
                            dotRadius = 10.dp,
                            dotColor = Color.Green,
                            dotTrailCount = 5,
                            animDelay = 200,
                            animDuration = 1200
                        )

                        9 -> ZeeLoader(
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

