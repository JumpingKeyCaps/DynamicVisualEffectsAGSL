package com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.composition

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.viewmodel.WaterViewModel

@Composable
fun WaterEffectBitmapShader(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    viewModel: WaterViewModel = viewModel(),
    @RawRes shaderResId: Int
) {
    val context = LocalContext.current

    // Load shader code once
    val shaderCode = remember {
        context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }

    // Track animation time in seconds (consistent with shader)
    var currentTimeSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            withFrameNanos { frameTime ->
                currentTimeSeconds = (frameTime - startTime) / 1_000_000_000f
            }
            // Cleanup using seconds instead of millis
            viewModel.cleanupWaves(currentTimeSeconds)
            kotlinx.coroutines.delay(16) // 60fps limit
        }
    }

    val waves by viewModel.waves.collectAsState()

    var composableSize by remember { mutableStateOf(Size.Zero) }

    // Get current shader uniforms from ViewModel
    val currentShaderParams = viewModel.getShaderUniforms(currentTimeSeconds)

    // Set uniforms only if size is known
    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uTime", currentTimeSeconds)
            shader.setFloatUniform("uGlobalDamping", currentShaderParams.globalDamping)
            shader.setFloatUniform("uMinAmplitudeThreshold", currentShaderParams.minAmplitudeThreshold)
            shader.setIntUniform("uNumWaves", currentShaderParams.numWaves)
            shader.setFloatUniform("uWaveOrigins", currentShaderParams.origins)
            shader.setFloatUniform("uWaveAmplitudes", currentShaderParams.amplitudes)
            shader.setFloatUniform("uWaveFrequencies", currentShaderParams.frequencies)
            shader.setFloatUniform("uWaveSpeeds", currentShaderParams.speeds)
            shader.setFloatUniform("uWaveStartTimes", currentShaderParams.startTimes)
        }
    }

    val renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "inputShader").asComposeRenderEffect()

    val touchModifier = Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    if (change.pressed) {
                        viewModel.addWave(change.position, change.id.value.toInt(), currentTimeSeconds)
                    }
                }
                while (true) {
                    val move = awaitPointerEvent()
                    move.changes.forEach { change ->
                        if (change.pressed) {
                            viewModel.addWave(change.position, change.id.value.toInt(), currentTimeSeconds)
                        }
                    }
                    if (move.changes.all { !it.pressed }) break
                }
            }
        }
    }

    Image(
        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { composableSize = it.toSize() }
            .then(touchModifier)
            .graphicsLayer(renderEffect = renderEffect)
    )
}