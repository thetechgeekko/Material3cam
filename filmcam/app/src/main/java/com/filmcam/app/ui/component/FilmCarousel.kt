package com.filmcam.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filmcam.app.domain.FilmSimulation
import kotlinx.coroutines.launch

// Google I/O 2026 - Snapping Film Carousel
// LazyRow with snap fling behavior mimicking analog camera film selection

@Composable
fun FilmCarousel(
    simulations: List<FilmSimulation>,
    selectedSimulation: FilmSimulation,
    onSimulationSelected: (FilmSimulation) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Calculate center item for snapping
    val visibleItemIndex by remember {
        derivedStateOf {
            if (listState.layoutInfo.totalItemsCount == 0) return@derivedStateOf 0
            val centerOffset = listState.layoutInfo.viewportStartOffset + 
                listState.layoutInfo.viewportSize / 2
            listState.layoutInfo.visibleItemsInfo
                .minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - centerOffset) }
                ?.index ?: 0
        }
    }
    
    LaunchedEffect(visibleItemIndex) {
        if (visibleItemIndex in simulations.indices) {
            val currentSim = simulations[visibleItemIndex]
            if (currentSim != selectedSimulation) {
                haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                onSimulationSelected(currentSim)
            }
        }
    }
    
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 64.dp)
    ) {
        items(simulations.size) { index ->
            val simulation = simulations[index]
            FilmCanisterItem(
                simulation = simulation,
                isSelected = simulation == selectedSimulation,
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(index)
                    }
                }
            )
        }
    }
}

@Composable
private fun FilmCanisterItem(
    simulation: FilmSimulation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Surface(
        modifier = Modifier
            .size(width = 120.dp, height = 120.dp)
            .clip(RoundedCornerShape(24.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = simulation.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
