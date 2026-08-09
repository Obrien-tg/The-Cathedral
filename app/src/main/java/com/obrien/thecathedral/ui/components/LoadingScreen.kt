package com.obrien.thecathedral.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.obrien.thecathedral.R
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment

@Composable
fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonasteryBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Sunflower spinning
        Image(
            painter = painterResource(id = R.drawable.ic_sunflower),
            contentDescription = "Loading",
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(24.dp))

        CircularProgressIndicator(
            color = CathedralGold,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading your Cathedral...",
            color = Parchment.copy(alpha = 0.7f)
        )
    }
}
