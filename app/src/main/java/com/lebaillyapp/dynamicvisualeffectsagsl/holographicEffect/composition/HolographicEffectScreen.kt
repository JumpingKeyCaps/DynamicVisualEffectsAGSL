package com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicEffectBitmapShader(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,
    intensity: Float = 1.0f,
    shimmerSpeed: Float = 1.0f,
    tiltAngle: Float = 0.0f
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
            kotlinx.coroutines.delay(16) // 60fps limit
        }
    }

    var composableSize by remember { mutableStateOf(Size.Zero) }

    // Calculate dynamic parameters based on time and tilt
    val dynamicTiltX = remember(currentTimeSeconds, tiltAngle) {
        cos(currentTimeSeconds * 0.3f + tiltAngle) * 0.5f
    }
    val dynamicTiltY = remember(currentTimeSeconds, tiltAngle) {
        sin(currentTimeSeconds * 0.2f + tiltAngle) * 0.3f
    }

    // Set uniforms only if size is known
    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uTime", currentTimeSeconds)
            shader.setFloatUniform("uIntensity", intensity)
            shader.setFloatUniform("uShimmerSpeed", shimmerSpeed)
            shader.setFloatUniform("uTiltX", dynamicTiltX)
            shader.setFloatUniform("uTiltY", dynamicTiltY)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)
        }
    }

    val renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "inputShader").asComposeRenderEffect()

    Image(
        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { composableSize = it.toSize() }
            .graphicsLayer(renderEffect = renderEffect)
    )
}