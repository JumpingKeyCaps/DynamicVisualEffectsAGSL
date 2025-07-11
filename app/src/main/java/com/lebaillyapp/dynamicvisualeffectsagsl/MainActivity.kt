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
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            //default bitmap
            val bitmap = ImageBitmap.imageResource(id = R.drawable.demopic_g)

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

                    /**
                        HolographicEffectBitmapShader(
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_base_shader,
                            intensity = 0.99f,        // Intensité globale
                            shimmerSpeed = 1.2f,     // Vitesse d'animation
                            tiltAngle = 0.5f         // Angle de base pour les reflets
                        )
                        */

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



                    }
                }
            }
        }
    }
}
