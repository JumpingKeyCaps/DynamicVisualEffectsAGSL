package com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect

import android.app.ActivityManager
import android.content.Context
import android.graphics.RuntimeShader
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import androidx.core.content.edit
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.model.TopographicFlowConfig
import java.io.File

@Composable
fun TopographicFlowShaderWithControls(
    modifier: Modifier = Modifier,
    @RawRes shaderResId: Int,
    shaderName: String
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("topo_settings", Context.MODE_PRIVATE) }

    var lineDensity by remember { mutableFloatStateOf(prefs.getFloat("lineDensity", 15f)) }
    var lineThickness by remember { mutableFloatStateOf(prefs.getFloat("lineThickness", 0.05f)) }
    var noiseScale by remember { mutableFloatStateOf(prefs.getFloat("noiseScale", 1f)) }
    var noiseIntensity by remember { mutableFloatStateOf(prefs.getFloat("noiseIntensity", 0.25f)) }
    var speedX by remember { mutableFloatStateOf(prefs.getFloat("speedX", 0.20f)) }
    var speedY by remember { mutableFloatStateOf(prefs.getFloat("speedY", 0.05f)) }
    var glowWidth by remember { mutableFloatStateOf(prefs.getFloat("glowWidth", 1.1f)) }
    var glowContrast by remember { mutableFloatStateOf(prefs.getFloat("glowContrast", 0.5f)) }

    val config = TopographicFlowConfig(
        lineDensity = lineDensity,
        lineThickness = lineThickness,
        noiseScale = noiseScale,
        noiseIntensity = noiseIntensity,
        speedX = speedX,
        speedY = speedY,
        glowWidthMultiplier = glowWidth,
        glowContrast = glowContrast
    )

    val isPinned = remember {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        TopographicFlowShaderComposable(
            modifier = Modifier.matchParentSize(),
            config = config,
            shaderResId = shaderResId,
            shaderName = shaderName
        )

        // Contrôles en overlay
        if (!isPinned) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ControlSlider("Density", lineDensity, 5f..50f) { 
                            lineDensity = it
                            prefs.edit { putFloat("lineDensity", it) }
                        }
                        ControlSlider("Thickness", lineThickness, 0.01f..0.5f) { 
                            lineThickness = it
                            prefs.edit { putFloat("lineThickness", it) }
                        }
                        ControlSlider("Noise Scale", noiseScale, 0.1f..5f) { 
                            noiseScale = it
                            prefs.edit { putFloat("noiseScale", it) }
                        }
                        ControlSlider("Noise Intensity", noiseIntensity, 0f..1f) { 
                            noiseIntensity = it
                            prefs.edit { putFloat("noiseIntensity", it) }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ControlSlider("Speed X", speedX, -1f..1f) { 
                            speedX = it
                            prefs.edit { putFloat("speedX", it) }
                        }
                        ControlSlider("Speed Y", speedY, -1f..1f) { 
                            speedY = it
                            prefs.edit { putFloat("speedY", it) }
                        }
                        ControlSlider("Glow Width", glowWidth, 0.5f..5f) { 
                            glowWidth = it
                            prefs.edit { putFloat("glowWidth", it) }
                        }
                        ControlSlider("Glow Contrast", glowContrast, 0.1f..2f) { 
                            glowContrast = it
                            prefs.edit { putFloat("glowContrast", it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopographicFlowShaderComposable(
    modifier: Modifier = Modifier,
    config: TopographicFlowConfig,
    @RawRes shaderResId: Int,
    shaderName: String
) {
    val context = LocalContext.current

    val file = remember(shaderName) { File(context.filesDir, "$shaderName.agsl") }
    val shaderSource = remember(shaderName, file.lastModified()) {
        if (file.exists()) {
            file.readText()
        } else {
            context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
        }
    }

    val shader = remember(shaderSource) {
        try {
            RuntimeShader(shaderSource)
        } catch (_: Exception) {
            val defaultCode = context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
            RuntimeShader(defaultCode)
        }
    }

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { millis ->
                time = (millis / 1000f) % 3600f
            }
        }
    }

    val colorLine = Color(0xFFC6FF00)
    val colorBg = Color(0xFF000000)

    Canvas(modifier = modifier.background(colorBg)) {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time)
        shader.setFloatUniform("lineColor", colorLine.red, colorLine.green, colorLine.blue, colorLine.alpha)
        shader.setFloatUniform("bgColor", colorBg.red, colorBg.green, colorBg.blue, colorBg.alpha)

        shader.setFloatUniform("LINE_DENSITY", config.lineDensity)
        shader.setFloatUniform("LINE_THICKNESS", config.lineThickness)
        shader.setFloatUniform("NOISE_SCALE", config.noiseScale)
        shader.setFloatUniform("NOISE_INTENSITY", config.noiseIntensity)
        shader.setFloatUniform("SPEED_X", config.speedX)
        shader.setFloatUniform("SPEED_Y", config.speedY)
        shader.setFloatUniform("GLOW_WIDTH_MULTIPLIER", config.glowWidthMultiplier)
        shader.setFloatUniform("GLOW_CONTRAST", config.glowContrast)

        drawRect(ShaderBrush(shader))
    }
}

@Composable
private fun ControlSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("$label: ${String.format("%.2f", value)}", color = Color.White, fontSize = 12.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.height(24.dp)
        )
    }
}
