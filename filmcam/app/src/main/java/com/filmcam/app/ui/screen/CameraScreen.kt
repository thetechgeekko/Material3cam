package com.filmcam.app.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.filmcam.app.domain.ActivePipeline
import com.filmcam.app.domain.ColorSpace
import com.filmcam.app.domain.FilmSimulation
import com.filmcam.app.ui.component.ExpressiveWaveProgress
import com.filmcam.app.ui.component.FilmCarousel
import com.filmcam.app.ui.component.LumaCurveCanvas
import com.filmcam.app.ui.component.TactileHorizontalSlider
import com.filmcam.app.ui.animation.viewfinderMorphSpring
import kotlin.math.roundToInt

// Google I/O 2026 - Main Camera Screen
// Implements "Live View, Zero Obstruction" architecture with morphing viewfinder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    var activePipeline by remember { mutableStateOf(ActivePipeline.Simulation) }
    var selectedFilm by remember { mutableStateOf(FilmSimulation.KodakPortra400) }
    var selectedColorSpace by remember { mutableStateOf(ColorSpace.Rec709) }
    var isProcessing by remember { mutableStateOf(false) }
    
    // Camera permission handling
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    SharedTransitionLayout {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Viewfinder - morphs between full-screen and floating card
                    AnimatedContent(
                        targetState = activePipeline,
                        label = "viewfinderMorph",
                        transitionSpec = {
                            spring(animationSpec = viewfinderMorphSpring)
                        }
                    ) { pipeline ->
                        when (pipeline) {
                            ActivePipeline.Simulation -> {
                                // Capture State - Full screen viewfinder
                                CameraViewfinder(
                                    modifier = Modifier.fillMaxSize(),
                                    hasPermission = hasCameraPermission,
                                    context = context,
                                    lifecycleOwner = lifecycleOwner
                                )
                            }
                            ActivePipeline.Lut -> {
                                // Tuning State - Floating card at 60% height
                                CameraViewfinder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((60).dp)
                                        .align(Alignment.TopCenter)
                                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                                    hasPermission = hasCameraPermission,
                                    context = context,
                                    lifecycleOwner = lifecycleOwner
                                )
                            }
                        }
                    }
                    
                    // Processing indicator below viewfinder
                    if (activePipeline == ActivePipeline.Lut) {
                        ExpressiveWaveProgress(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .zIndex(1f),
                            isProcessing = isProcessing
                        )
                    }
                    
                    // Master Control Deck - Bottom 40%
                    ControlDeck(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        activePipeline = activePipeline,
                        onPipelineChanged = { activePipeline = it },
                        selectedFilm = selectedFilm,
                        onFilmSelected = { selectedFilm = it },
                        selectedColorSpace = selectedColorSpace,
                        onColorSpaceSelected = { selectedColorSpace = it },
                        onProcessingToggle = { isProcessing = it }
                    )
                }
            }
        )
    }
}

@Composable
private fun CameraViewfinder(
    modifier: Modifier = Modifier,
    hasPermission: Boolean,
    context: android.content.Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera permission required",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlDeck(
    modifier: Modifier = Modifier,
    activePipeline: ActivePipeline,
    onPipelineChanged: (ActivePipeline) -> Unit,
    selectedFilm: FilmSimulation,
    onFilmSelected: (FilmSimulation) -> Unit,
    selectedColorSpace: ColorSpace,
    onColorSpaceSelected: (ColorSpace) -> Unit,
    onProcessingToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Master Toggle - SingleChoiceSegmentedButtonRow
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = activePipeline == ActivePipeline.Simulation,
                    onClick = { onPipelineChanged(ActivePipeline.Simulation) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(
                        text = "Simulation",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                SegmentedButton(
                    selected = activePipeline == ActivePipeline.Lut,
                    onClick = { onPipelineChanged(ActivePipeline.Lut) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(
                        text = "LUT",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // AnimatedContent for pipeline-specific controls
            AnimatedContent(
                targetState = activePipeline,
                label = "pipelineControls"
            ) { pipeline ->
                when (pipeline) {
                    ActivePipeline.Simulation -> {
                        // Branch Simulation - Film roll carousel + parameters
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilmCarousel(
                                simulations = FilmSimulation.entries,
                                selectedSimulation = selectedFilm,
                                onSimulationSelected = onFilmSelected
                            )
                            
                            // Bloom parameter
                            Text(
                                text = "Bloom",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TactileHorizontalSlider(
                                value = 50f,
                                onValueChange = {},
                                valueRange = 0f..100f
                            )
                            
                            // Grain parameter
                            Text(
                                text = "Grain",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TactileHorizontalSlider(
                                value = 30f,
                                onValueChange = {},
                                valueRange = 0f..100f
                            )
                        }
                    }
                    ActivePipeline.Lut -> {
                        // Branch LUT - Color space dropdown + curves graph
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Color space selector
                            Text(
                                text = "Color Space: ${selectedColorSpace.displayName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            // Simple color space toggle
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorSpace.entries.take(3).forEach { space ->
                                    Surface(
                                        onClick = { onColorSpaceSelected(space) },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (selectedColorSpace == space)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                    ) {
                                        Text(
                                            text = space.displayName,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(12.dp),
                                            color = if (selectedColorSpace == space)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            
                            // Luma curve canvas
                            Text(
                                text = "Luma Curve",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            LumaCurveCanvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                onCurveChanged = { onProcessingToggle(true) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ColorSpaceSelector(
    // Helper for color space selection UI
) {
    // Implemented inline in ControlDeck
}
