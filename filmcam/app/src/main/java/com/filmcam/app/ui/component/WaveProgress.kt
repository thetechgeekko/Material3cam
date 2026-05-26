package com.filmcam.app.ui.component

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.filmcam.app.ui.theme.WavePrimary
import com.filmcam.app.ui.theme.WaveSecondary
import com.filmcam.app.ui.theme.WaveTertiary

// Google I/O 2026 - Expressive Wave Progress Indicator
// Pulsing waveform layout for RAW frame processing indication

@Composable
fun ExpressiveWaveProgress(
    modifier: Modifier = Modifier,
    isProcessing: Boolean = true
) {
    if (!isProcessing) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "wavePulse")
    
    val wave1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    
    val wave2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )
    
    val wave3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, delayMillis = 600),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )
    
    val amplitude1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amp1"
    )
    
    val amplitude2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amp2"
    )
    
    val amplitude3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amp3"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wave dot 1
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = WavePrimary.copy(alpha = amplitude1)
            ) {}
            
            // Wave dot 2
            Surface(
                modifier = Modifier.size(10.dp),
                shape = CircleShape,
                color = WaveSecondary.copy(alpha = amplitude2)
            ) {}
            
            // Wave dot 3
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = WaveTertiary.copy(alpha = amplitude3)
            ) {}
        }
        
        // Animated waveform canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            
            // Draw multi-layered waveform
            for (layer in 0..2) {
                val color = when (layer) {
                    0 -> WavePrimary.copy(alpha = 0.6f * amplitude1)
                    1 -> WaveSecondary.copy(alpha = 0.5f * amplitude2)
                    else -> WaveTertiary.copy(alpha = 0.4f * amplitude3)
                }
                
                val phaseOffset = when (layer) {
                    0 -> wave1Offset
                    1 -> wave2Offset
                    else -> wave3Offset
                }
                
                val layerAmplitude = when (layer) {
                    0 -> height * 0.15f * amplitude1
                    1 -> height * 0.2f * amplitude2
                    else -> height * 0.1f * amplitude3
                }
                
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, centerY)
                    
                    for (x in 0..width.toInt() step 2) {
                        val normalizedX = (x + phaseOffset * width / 50) % width
                        val sineValue = kotlin.math.sin((normalizedX / width) * kotlin.math.PI * 4)
                        val y = centerY + sineValue * layerAmplitude
                        lineTo(x.toFloat(), y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
