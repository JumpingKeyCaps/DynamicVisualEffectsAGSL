package com.lebaillyapp.dynamicvisualeffectsagsl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.EnhancedHolographicEffectShader
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.OptimizedHolographicCardEffect
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.UltraRealisticHolographicEffectShader
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            //default bitmap
            val bitmap = ImageBitmap.imageResource(id = R.drawable.democard1)

            DynamicVisualEffectsAGSLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {

                        // [1] - Water Effect composition screen

                        /**

                                WaterEffectBitmapShader(
                                    modifier = Modifier.fillMaxSize(),
                                    bitmap = bitmap,
                                    shaderResId = R.raw.water_shader
                                )

                        */


                        // [2] - Fire Effect composition screen

                        /**
                        FireEffectScreen(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.fire_shader
                        )

                        */

                        // [5] - Holographic Effect composition screen

                    /** v1
                        HolographicEffectBitmapShader(
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_base_shader,
                            intensity = 0.99f,        // Intensité globale
                            shimmerSpeed = 1.2f,     // Vitesse d'animation
                            tiltAngle = 0.5f         // Angle de base pour les reflets
                        )
                        */


                        /** v2

                        EnhancedHolographicEffectShader(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_shader, // Chemin vers votre nouveau shader
                            effectIntensity = 1.0f,
                            shininess = 150.0f,
                            rainbowScale = 1.5f,
                            rainbowOffset = 0.0f,
                            patternDensity = 25.0f,
                            patternVisibility = 0.8f,
                            sparkleDensity = 70.0f,
                            sparklePower = 10.0f,
                            sparkleIntensity = 0.7f,
                            chromaticAberrationStrength = 20.0f
                        )
                        */

                        /**
                        UltraRealisticHolographicEffectShader(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_realistic_shader,
                            // Paramètres optimisés pour un rendu ultra-réaliste
                            effectIntensity = 0.8f,
                            shininess = 80.0f,
                            roughness = 0.6f, // rugositer (force de leffet metalic)
                            fresnelPower = 3.0f,
                            rainbowScale = 2.5f,
                            rainbowOffset = 0.2f,
                            spectrumLayers = 3.0f,
                            normalStrength = 2.0f, // intensité du relief bruit (degradation de la carte)
                            metallic = 2.0f, // intensité du relief metalic
                            microDetailScale = 50.0f,
                            subsurfaceStrength = 0.3f,
                            subsurfaceThickness = 0.5f,
                            patternDensity = 40.0f,
                            patternVisibility = 0.6f,
                            sparkleDensity = 120.0f,
                            sparklePower = 15.0f,
                            sparkleIntensity = 0.4f,
                            sparkleSpeed = 1.0f,
                            chromaticAberrationStrength = 0.3f,
                            animationSpeed = 1.0f,
                            pulseIntensity = 0.1f
                        )
                        */

                        OptimizedHolographicCardEffect(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_card_shader,
                            hologramStrength = 1.0f,
                            iridescentPatternScale = 50.0f,
                            iridescentDarknessThreshold = 0.2f,
                            chromaticAberrationStrength = 0.4f,
                            reflectionRoughness = 0.5f,
                            sparkleVisibility = 0.8f,
                            animationSpeed = 1.0f
                        )


                    }
                }
            }
        }
    }
}
