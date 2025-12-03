package com.lebaillyapp.dynamicvisualeffectsagsl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.HolographicEffectBitmapShader
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.OptimizedHolographicCardEffect
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.UltraRealisticHolographicEffectShader
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.TopographicFlowShader
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.TopographicFlowShaderWithControls
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.composition.WaterEffectBitmapShader

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {

        composable("menu") {
            EffectsMenu(
                onSelect = { route -> navController.navigate(route) }
            )
        }

        composable("water") {
            val bitmap = ImageBitmap.imageResource(R.drawable.demopic_e)
            WaterEffectBitmapShader(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                bitmap = bitmap,
                shaderResId = R.raw.water_shader
            )
        }

        composable("holo_base") {
            val bitmap = ImageBitmap.imageResource(R.drawable.demopic_d)
            OptimizedHolographicCardEffect(
                modifier = Modifier.fillMaxSize(),
                bitmap = bitmap,
                shaderResId = R.raw.holographic_rainbow
            )
        }

        composable("holo_Iridescent") {
            val bitmap = ImageBitmap.imageResource(R.drawable.demopic_e)
            UltraRealisticHolographicEffectShader(
                modifier = Modifier.fillMaxSize(),
                bitmap = bitmap,
                shaderResId = R.raw.holographic_realistic_shader,
                effectIntensity = 6.8f,
                fresnelPower = 6.0f,
                rainbowScale = 1.2f,
                rainbowOffset = 0.2f,
                normalStrength = 2.0f,
                microDetailScale = 45.0f
            )
        }

        composable("holo_card") {
            val bitmap = ImageBitmap.imageResource(R.drawable.de2)
            OptimizedHolographicCardEffect(
                modifier = Modifier.fillMaxSize(),
                bitmap = bitmap,
                shaderResId = R.raw.holographic_card_shader
            )
        }

        composable("topo") {
            TopographicFlowShader(modifier = Modifier.fillMaxSize())
        }

        composable("topo_controls") {
            TopographicFlowShaderWithControls(modifier = Modifier.fillMaxSize())
        }

        composable("fire") {
           //later !
        }
    }
}
