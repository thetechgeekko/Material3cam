package com.filmcam.app.ui.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

// Google I/O 2026 - Physics-based Spring Animation Tokens
// Used for viewfinder morphing and tactile interactions

// Viewfinder morph spring - bouncy, organic transition
val viewfinderMorphSpring: AnimationSpec<Float> = spring(
    dampingRatio = 0.75f,
    stiffness = Spring.StiffnessMediumLow
)

// Control deck elevation spring - subtle lift
val controlDeckElevationSpring: AnimationSpec<Float> = spring(
    dampingRatio = 0.8f,
    stiffness = Spring.StiffnessMedium
)

// Film carousel snap spring - mechanical click feel
val filmSnapSpring: AnimationSpec<Float> = spring(
    dampingRatio = 0.6f,
    stiffness = Spring.StiffnessHigh
)

// Luma curve node spring - smooth but responsive
val curveNodeSpring: AnimationSpec<Float> = spring(
    dampingRatio = 0.85f,
    stiffness = Spring.StiffnessMedium
)

// Processing indicator pulse spring - gentle breathing
val wavePulseSpring: AnimationSpec<Float> = spring(
    dampingRatio = 0.9f,
    stiffness = Spring.StiffnessLow
)
