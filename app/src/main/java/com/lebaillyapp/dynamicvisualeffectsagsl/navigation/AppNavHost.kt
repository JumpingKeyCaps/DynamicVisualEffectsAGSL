package com.lebaillyapp.dynamicvisualeffectsagsl.navigation

import android.app.ActivityManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import com.lebaillyapp.dynamicvisualeffectsagsl.navigation.ShaderEditorScreen
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.OptimizedHolographicCardEffect
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition.UltraRealisticHolographicEffectShader
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.TopographicFlowShader
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.TopographicFlowShaderWithControls
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.composition.WaterEffectBitmapShader
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var userSelectedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            userSelectedBitmap = bitmap?.asImageBitmap()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {

        composable("menu") {
            EffectsMenu(
                onSelect = { route -> navController.navigate(route) },
                onEdit = { shaderName -> navController.navigate("editor/$shaderName") }
            )
        }

        composable(
            "editor/{shaderName}",
            arguments = listOf(navArgument("shaderName") { type = NavType.StringType })
        ) { backStackEntry ->
            val shaderName = backStackEntry.arguments?.getString("shaderName") ?: ""
            ShaderEditorScreen(
                shaderName = shaderName,
                onBack = { navController.popBackStack() }
            )
        }

        composable("water") {
            val defaultBitmap = ImageBitmap.imageResource(R.drawable.demopic_e)
            EffectContainer(onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                WaterEffectBitmapShader(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    bitmap = userSelectedBitmap ?: defaultBitmap,
                    shaderResId = R.raw.water_shader,
                    shaderName = "water_shader"
                )
            }
        }

        composable("holo_base") {
            val defaultBitmap = ImageBitmap.imageResource(R.drawable.demopic_d)
            EffectContainer(onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                OptimizedHolographicCardEffect(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = userSelectedBitmap ?: defaultBitmap,
                    shaderResId = R.raw.holographic_rainbow,
                    shaderName = "holographic_rainbow",
                    microDetailScale = 2f
                )
            }
        }

        composable("holo_Iridescent") {
            val defaultBitmap = ImageBitmap.imageResource(R.drawable.demopic_e)
            EffectContainer(onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                UltraRealisticHolographicEffectShader(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = userSelectedBitmap ?: defaultBitmap,
                    shaderResId = R.raw.holographic_realistic_shader,
                    shaderName = "holographic_realistic_shader",
                    effectIntensity = 6.8f,
                    fresnelPower = 6.0f,
                    rainbowScale = 1.2f,
                    rainbowOffset = 0.2f,
                    normalStrength = 2.0f,
                    microDetailScale = 45.0f
                )
            }
        }

        composable("holo_card") {
            val defaultBitmap = ImageBitmap.imageResource(R.drawable.de2)
            EffectContainer(onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                OptimizedHolographicCardEffect(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = userSelectedBitmap ?: defaultBitmap,
                    shaderResId = R.raw.holographic_card_shader,
                    shaderName = "holographic_card_shader"
                )
            }
        }

        composable("topo") {
            TopographicFlowShader(
                modifier = Modifier.fillMaxSize(),
                shaderResId = R.raw.topographicflow_shader,
                shaderName = "topographicflow_shader"
            )
        }

        composable("topo_controls") {
            TopographicFlowShaderWithControls(
                modifier = Modifier.fillMaxSize(),
                shaderResId = R.raw.topographicflow_shader,
                shaderName = "topographicflow_shader"
            )
        }

        composable("fire") {
            //later !
        }
    }
}

@Composable
fun EffectContainer(onPickImage: () -> Unit, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isPinned = remember {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (!isPinned) {
            FloatingActionButton(
                onClick = onPickImage,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
                containerColor = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Star, contentDescription = "Pick Image")
            }
        }
    }
}
