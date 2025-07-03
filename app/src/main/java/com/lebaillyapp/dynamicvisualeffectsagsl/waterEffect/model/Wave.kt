package com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.model

import androidx.compose.ui.geometry.Offset

data class Wave(
    val origin: Offset,
    val startTime: Float, // Changé de Long à Float pour être cohérent avec les secondes
    val amplitude: Float,
    val frequency: Float,
    val speed: Float
)