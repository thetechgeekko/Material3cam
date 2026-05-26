package com.filmcam.app.domain

// Google I/O 2026 - Active Pipeline State
// Controls the Master Toggle between Simulation and LUT workflows

enum class ActivePipeline {
    Simulation,
    Lut
}

// Film simulation types for the carousel
enum class FilmSimulation(val displayName: String) {
    KodakPortra400("Portra 400"),
    FujifilmProvia("Provia 100F"),
    IlfordHP5("Ilford HP5+"),
    Cinestill800T("CineStill 800T"),
    AgfaVista("Agfa Vista 400")
}

// LUT color space options
enum class ColorSpace(val displayName: String) {
    Rec709("Rec. 709"),
    DCIP3("DCI-P3"),
    Rec2020("Rec. 2020"),
    ACES("ACEScc"),
    LogC("ARRI LogC")
}
