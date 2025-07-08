package com.lebaillyapp.dynamicvisualeffectsagsl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.HolographicEffectBitmapShader
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            //default bitmap
            val bitmap = ImageBitmap.imageResource(id = R.drawable.demopic_c)

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


                        HolographicEffectBitmapShader(
                            bitmap = bitmap,
                            shaderResId = R.raw.holographic_base_shader,
                            intensity = 0.8f,        // Intensité globale
                            shimmerSpeed = 1.2f,     // Vitesse d'animation
                            tiltAngle = 0.5f         // Angle de base pour les reflets
                        )



                    }
                }
            }
        }
    }
}
