package com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.model.TopographicFlowConfig


@Composable
fun TopographicFlowShader(modifier: Modifier) {
    val context = LocalContext.current
    val config = remember {
        TopographicFlowConfig(
            lineDensity = 15.0f,
            lineThickness = 0.05f,
            noiseScale = 1.0f,
            noiseIntensity = 0.25f,
            speedX = 0.20f,
            speedY = 0.05f,
            glowWidthMultiplier = 1.1f,
            glowContrast = 0.5f
        )
    } // Valeurs par défaut finales

    // 1. Chargement du shader depuis res/raw
    val shaderSource = remember {
        try {
            // Utiliser R.raw.liquidflow (ajustez si le nom de fichier est différent)
            context.resources
                .openRawResource(R.raw.topographicflow_shader)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            // Gestion d'erreur
            throw IllegalStateException("Impossible de charger le shader AGSL.", e)
        }
    }

    // 2. Création du RuntimeShader
    val shader = remember(shaderSource) { RuntimeShader(shaderSource) }

    // 3. Animation du temps
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { millis ->
                time = (millis / 1000f) % 3600f
            }
        }
    }

    val colorLine = remember { Color(0xFFFF9900) } // La couleur orange/or
    val colorBg = remember { Color(0xFF0A0A10) } // Le fond noir sombre

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorBg),
        contentAlignment = Alignment.Center
    ) {
        // 4. Canvas avec ShaderBrush
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Configuration des uniforms AGSL à chaque frame
            // Uniforms système
            shader.setFloatUniform("resolution", size.width, size.height)
            shader.setFloatUniform("time", time)
            // Uniforms de couleur
            shader.setFloatUniform(
                "lineColor",
                colorLine.red, colorLine.green, colorLine.blue, colorLine.alpha
            )
            shader.setFloatUniform("bgColor",colorBg.red, colorBg.green, colorBg.blue, colorBg.alpha)
            // Uniforms de configuration (Dynamiques)
            shader.setFloatUniform("LINE_DENSITY", config.lineDensity)
            shader.setFloatUniform("LINE_THICKNESS", config.lineThickness)
            shader.setFloatUniform("NOISE_SCALE", config.noiseScale)
            shader.setFloatUniform("NOISE_INTENSITY", config.noiseIntensity)
            shader.setFloatUniform("SPEED_X", config.speedX)
            shader.setFloatUniform("SPEED_Y", config.speedY)
            shader.setFloatUniform("GLOW_WIDTH_MULTIPLIER", config.glowWidthMultiplier)
            shader.setFloatUniform("GLOW_CONTRAST", config.glowContrast)
            // Dessin avec le shader
            drawRect(ShaderBrush(shader))
        }
    }
}