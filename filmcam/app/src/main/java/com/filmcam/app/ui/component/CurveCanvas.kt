package com.filmcam.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.filmcam.app.ui.animation.curveNodeSpring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch

// Google I/O 2026 - Luma Curve Canvas Component
// Interactive three-node contrast curve with tactile haptic feedback

data class CurvePoint(
    val x: Float,
    var y: Float
)

@Composable
fun LumaCurveCanvas(
    modifier: Modifier = Modifier,
    onCurveChanged: (List<Float>) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var lowPoint by remember { mutableStateOf(CurvePoint(0f, 1f)) }
    var midPoint by remember { mutableStateOf(CurvePoint(0.5f, 0.5f)) }
    var highPoint by remember { mutableStateOf(CurvePoint(1f, 0f)) }
    
    val canvasSize = remember { mutableStateOf(Offset.Zero) }
    var lastThresholdCrossed by remember { mutableStateOf<Int?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(lowPoint, midPoint, highPoint) {
                    detectDragGestures { change, dragAmount ->
                        val dragY = -dragAmount.y / size.height
                        val dragX = dragAmount.x / size.width
                        
                        // Determine which point is being dragged based on proximity
                        val touchPos = change.position
                        val normX = touchPos.x / size.width
                        val normY = 1f - (touchPos.y / size.height)
                        
                        val distLow = kotlin.math.sqrt(
                            (normX - lowPoint.x) * (normX - lowPoint.x) + 
                            (normY - lowPoint.y) * (normY - lowPoint.y)
                        )
                        val distMid = kotlin.math.sqrt(
                            (normX - midPoint.x) * (normX - midPoint.x) + 
                            (normY - midPoint.y) * (normY - midPoint.y)
                        )
                        val distHigh = kotlin.math.sqrt(
                            (normX - highPoint.x) * (normX - highPoint.x) + 
                            (normY - highPoint.y) * (normY - highPoint.y)
                        )
                        
                        val minDist = minOf(distLow, distMid, distHigh)
                        
                        if (minDist < 0.15f) { // Touch threshold
                            when {
                                distLow == minDist -> {
                                    lowPoint.y = (lowPoint.y + dragY).coerceIn(0f, 1f)
                                    // Haptic feedback on threshold crossing
                                    val gridStep = (lowPoint.y * 10).toInt()
                                    if (lastThresholdCrossed != gridStep) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        lastThresholdCrossed = gridStep
                                    }
                                }
                                distMid == minDist -> {
                                    midPoint.y = (midPoint.y + dragY).coerceIn(0f, 1f)
                                    midPoint.x = (midPoint.x + dragX).coerceIn(0.1f, 0.9f)
                                    val gridStep = (midPoint.y * 10).toInt()
                                    if (lastThresholdCrossed != gridStep) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        lastThresholdCrossed = gridStep
                                    }
                                }
                                distHigh == minDist -> {
                                    highPoint.y = (highPoint.y + dragY).coerceIn(0f, 1f)
                                    val gridStep = (highPoint.y * 10).toInt()
                                    if (lastThresholdCrossed != gridStep) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        lastThresholdCrossed = gridStep
                                    }
                                }
                            }
                            
                            // Notify curve changes
                            onCurveChanged(listOf(lowPoint.y, midPoint.y, highPoint.y))
                        }
                    }
                }
        ) {
            canvasSize.value = Offset(size.width, size.height)
            
            val width = size.width
            val height = size.height
            
            // Draw coordinate grid
            drawLine(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                start = Offset(0f, height),
                end = Offset(width, 0f),
                strokeWidth = 1.dp.toPx()
            )
            
            // Draw grid lines
            for (i in 1..4) {
                val pos = (i * width) / 5f
                drawLine(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    start = Offset(pos, 0f),
                    end = Offset(pos, height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    start = Offset(0f, pos),
                    end = Offset(width, pos),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // Draw smooth contrast curve using quadratic bezier
            val path = Path().apply {
                moveTo(0f, height) // Start at bottom-left
                quadraticBezierTo(
                    (lowPoint.x * width + midPoint.x * width) / 2f,
                    height - (lowPoint.y * height + midPoint.y * height) / 2f,
                    midPoint.x * width,
                    height - (midPoint.y * height)
                )
                quadraticBezierTo(
                    (midPoint.x * width + highPoint.x * width) / 2f,
                    height - (midPoint.y * height + highPoint.y * height) / 2f,
                    highPoint.x * width,
                    height - (highPoint.y * height)
                )
            }
            
            drawPath(
                path = path,
                color = MaterialTheme.colorScheme.primary,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw control points
            drawCircle(
                color = MaterialTheme.colorScheme.primary,
                radius = 12.dp.toPx(),
                center = Offset(lowPoint.x * width, height - lowPoint.y * height)
            )
            drawCircle(
                color = MaterialTheme.colorScheme.secondary,
                radius = 12.dp.toPx(),
                center = Offset(midPoint.x * width, height - midPoint.y * height)
            )
            drawCircle(
                color = MaterialTheme.colorScheme.tertiary,
                radius = 12.dp.toPx(),
                center = Offset(highPoint.x * width, height - highPoint.y * height)
            )
        }
        
        // Point labels
        androidx.compose.material3.Text(
            text = "Low",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
        androidx.compose.material3.Text(
            text = "High",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

// Google I/O 2026 - Tactile Horizontal Slider
// Custom slider with precise haptic clicks on whole value changes

@Composable
fun TactileHorizontalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    label: String = ""
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var lastIntValue by remember { mutableStateOf(value.toInt()) }
    
    Slider(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            val currentInt = newValue.toInt()
            if (currentInt != lastIntValue) {
                scope.launch {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                lastIntValue = currentInt
            }
        },
        valueRange = valueRange,
        steps = steps,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
