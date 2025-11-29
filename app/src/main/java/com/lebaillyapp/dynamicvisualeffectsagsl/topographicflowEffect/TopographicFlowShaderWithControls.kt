package com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.dynamicvisualeffectsagsl.R
import com.lebaillyapp.dynamicvisualeffectsagsl.topographicflowEffect.model.TopographicFlowConfig

@Composable
fun TopographicFlowShaderWithControls(
    modifier: Modifier = Modifier
) {
    var lineDensity by remember { mutableFloatStateOf(15f) }
    var lineThickness by remember { mutableFloatStateOf(0.05f) }
    var noiseScale by remember { mutableFloatStateOf(1f) }
    var noiseIntensity by remember { mutableFloatStateOf(0.25f) }
    var speedX by remember { mutableFloatStateOf(0.20f) }
    var speedY by remember { mutableFloatStateOf(0.05f) }
    var glowWidth by remember { mutableFloatStateOf(1.1f) }
    var glowContrast by remember { mutableFloatStateOf(0.5f) }

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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        TopographicFlowShaderComposable(
            modifier = Modifier.matchParentSize(),
            config = config
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0x66000000))
                .padding(8.dp)
        ) {
            CompactSlider("Dens.", lineDensity, 1f, 80f) { lineDensity = it }
            CompactSlider("Thick.", lineThickness, 0.01f, 0.5f) { lineThickness = it }
            CompactSlider("N-Scale", noiseScale, -5f, 5f) { noiseScale = it }
            CompactSlider("N-Int.", noiseIntensity, -1f, 1f) { noiseIntensity = it }
            CompactSlider("SpeedX", speedX, -1f, 1f) { speedX = it }
            CompactSlider("SpeedY", speedY, -1f, 1f) { speedY = it }
            CompactSlider("GlowW", glowWidth, 0.0f, 10f) { glowWidth = it }
            CompactSlider("GlowC", glowContrast, 0f, 10f) { glowContrast = it }
        }
    }
}

@Composable
private fun CompactSlider(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onChange: (Float) -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        androidx.compose.material3.Text(
            text = "$label ${"%.2f".format(value)}",
            color = Color.White,
            fontSize = 10.sp
        )
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onChange,
            valueRange = min..max,
            modifier = Modifier.height(22.dp) // slider plus petit
        )
    }
}


@Composable
fun TopographicFlowShaderComposable(
    modifier: Modifier = Modifier,
    config: TopographicFlowConfig
) {
    val context = LocalContext.current

    val shaderSource = remember {
        context.resources.openRawResource(R.raw.topographicflow_shader)
            .bufferedReader()
            .use { it.readText() }
    }

    val shader = remember(shaderSource) { RuntimeShader(shaderSource) }

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { millis ->
                time = (millis / 1000f) % 3600f
            }
        }
    }

    val colorLine = Color(0xFFFF1744)
    val colorBg = Color(0xFF0A0A10)

    Canvas(modifier = modifier.background(colorBg)) {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time)

        shader.setFloatUniform(
            "lineColor",
            colorLine.red, colorLine.green, colorLine.blue, colorLine.alpha
        )
        shader.setFloatUniform(
            "bgColor",
            colorBg.red, colorBg.green, colorBg.blue, colorBg.alpha
        )

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