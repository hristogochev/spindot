package com.hristogochev.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView

class MainActivity : AppCompatActivity() {

    private lateinit var composeView: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComposeView(this).also {
            setContentView(it)
            composeView = it
        }
        composeView.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black), contentAlignment = Alignment.Center
            ) {
//                BounceLoader(
//                    ballRadius = 40.dp,
//                    ballColor = Color.Magenta,
//                    showShadow = true,
//                    shadowColor = Color.LightGray,
//                    animDuration = 2000
//                )
//                ClassicLoader(
//                    activeColor = Color.Magenta,
//                    inactiveColor = Color.White,
//                    radius = 60.dp,
//                    dotRadius = 20.dp,
//                    animDuration = 200,
//                    showRunningShadow = true,
//                    firstShadowColor = Color.Blue,
//                    secondShadowColor = Color.Cyan
//                )
//                FidgetLoader(
//                    modifier = Modifier,
//                    dotRadius = 30.dp,
//                    drawOnlyStroke = true,
//                    strokeWidth = 8.dp,
//                    firstDotColor = Color.Red,
//                    secondDotColor = Color.Blue,
//                    thirdDotColor = Color.Green,
//                    distanceMultiplier = 4
//                )
//                JumpLoader(
//                    modifier = Modifier,
//                    spacing = 5.dp,
//                    dotRadius = 10.dp,
//                    firstDotColor = Color.Red,
//                    secondDotColor = Color.Green,
//                    thirdDotColor = Color.Blue,
//                    animDuration = 500,
//                    firstDotDelay = 100,
//                    secondDotDelay = 200,
//                )
//                LightsLoader(
//                    modifier = Modifier,
//                    size = 4,
//                    spacing = 5.dp,
//                    dotRadius = 10.dp,
//                    dotColor = Color.Magenta
//                )

//                SpinningLoader(
//                    radius = 40.dp,
//                    dotRadius = 10.dp,
//                    dotColor = Color.Blue,
//                    animDuration = 4000
//                )
            }
        }
    }
}