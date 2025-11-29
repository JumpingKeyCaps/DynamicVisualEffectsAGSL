package com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.model

// ---  VALEURS de config du SHADER ---
data class TopographicFlowConfig(
    // Lignes et Bruit
    val lineDensity: Float = 15.0f,
    val lineThickness: Float = 0.05f,
    val noiseScale: Float = 1.0f,
    val noiseIntensity: Float = 0.25f,

    // Vitesse de Scroll
    val speedX: Float = 0.20f,
    val speedY: Float = 0.05f,

    // Glow
    val glowWidthMultiplier: Float = 1.1f,
    val glowContrast: Float = 0.5f
)