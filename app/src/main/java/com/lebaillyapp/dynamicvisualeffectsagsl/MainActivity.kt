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
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.HolographicEffectBitmapShader
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
            val bitmap = ImageBitmap.imageResource(id = R.drawable.de2)

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





                        /**v3
                        UltraRealisticHolographicEffectShader(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_realistic_shader,
                            // Paramètres pertinents pour l'effet arc-en-ciel modulé par Fresnel
                            effectIntensity = 6.8f,
                            fresnelPower = 6.0f,
                            rainbowScale = 1.2f,
                            rainbowOffset = 0.2f,
                            normalStrength = 2.0f,
                            microDetailScale = 45.0f
                        )


                         */


                        OptimizedHolographicCardEffect(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_card_shader,

                        )




                    }
                }
            }
        }
    }
}
