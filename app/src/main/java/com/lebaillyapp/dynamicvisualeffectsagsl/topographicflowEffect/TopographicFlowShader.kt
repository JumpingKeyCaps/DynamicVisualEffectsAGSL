package com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect

import android.graphics.RuntimeShader
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.model.TopographicFlowConfig
import java.io.File


@Composable
fun TopographicFlowShader(
    modifier: Modifier,
    @RawRes shaderResId: Int,
    shaderName: String = "topographicflow_shader"
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("topo_settings", android.content.Context.MODE_PRIVATE) }

    val config = remember(
        prefs.getFloat("lineDensity", 15f),
        prefs.getFloat("lineThickness", 0.05f),
        prefs.getFloat("noiseScale", 1f),
        prefs.getFloat("noiseIntensity", 0.25f),
        prefs.getFloat("speedX", 0.20f),
        prefs.getFloat("speedY", 0.05f),
        prefs.getFloat("glowWidth", 1.1f),
        prefs.getFloat("glowContrast", 0.5f)
    ) {
        TopographicFlowConfig(
            lineDensity = prefs.getFloat("lineDensity", 15f),
            lineThickness = prefs.getFloat("lineThickness", 0.05f),
            noiseScale = prefs.getFloat("noiseScale", 1f),
            noiseIntensity = prefs.getFloat("noiseIntensity", 0.25f),
            speedX = prefs.getFloat("speedX", 0.20f),
            speedY = prefs.getFloat("speedY", 0.05f),
            glowWidthMultiplier = prefs.getFloat("glowWidth", 1.1f),
            glowContrast = prefs.getFloat("glowContrast", 0.5f)
        )
    }

    // Load shader code: check internal storage first, then fallback to resources
    val file = remember(shaderName) { File(context.filesDir, "$shaderName.agsl") }
    val shaderSource = remember(shaderName, file.lastModified()) {
        if (file.exists()) {
            file.readText()
        } else {
            context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
        }
    }

    // 2. Création du RuntimeShader avec fallback
    val shader = remember(shaderSource) {
        try {
            RuntimeShader(shaderSource)
        } catch (_: Exception) {
            val defaultCode = context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
            RuntimeShader(defaultCode)
        }
    }

    // 3. Animation du temps
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { millis ->
                time = (millis / 1000f) % 3600f
            }
        }
    }

    val colorLine = remember { Color(0xFFFF9100) } // La couleur orange/or
    val colorBg = remember { Color(0xFF000000) } // Le fond noir sombre

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