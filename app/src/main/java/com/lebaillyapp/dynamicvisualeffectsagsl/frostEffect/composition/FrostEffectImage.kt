package com.lebaillyapp.dynamicvisualeffectsagsl.frostEffect.composition


import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RawRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize

@Composable
fun FrostEffectImage(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,
    frostIntensity: Float = 1.0f,
    frostSpeed: Float = 0.1f
) {
    val context = LocalContext.current
    val shaderCode = remember {
        context.resources.openRawResource(shaderResId)
            .bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }
    var composableSize by remember { mutableStateOf(Size.Zero) }

    var isTouching by remember { mutableStateOf(false) }
    var frostTime by remember { mutableStateOf(0f) }


    val maxTime = 15f // durée totale de l'animation en secondes (fixe)

    val animatable = remember { Animatable(0f) }

    LaunchedEffect(isTouching) {
        if (isTouching) {
            animatable.animateTo(maxTime, animationSpec = tween((maxTime * 1000).toInt()))
        } else {
            animatable.animateTo(0f, animationSpec = tween((maxTime * 1000).toInt()))
        }
    }

    LaunchedEffect(animatable) {
        snapshotFlow { animatable.value }.collect { value ->
            frostTime = value
        }
    }

    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                isTouching = true
                touchPosition = offset
                tryAwaitRelease()
                isTouching = false
            }
        )
    }

    val renderEffect = remember(shader, frostIntensity, frostTime, touchPosition) {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader")
                .asComposeRenderEffect()
        } else null
    }

    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)
            shader.setFloatUniform("uTime", frostTime)
            shader.setFloatUniform("uTouch", touchPosition.x, touchPosition.y)
            shader.setFloatUniform("uFrostIntensity", frostIntensity)
            shader.setFloatUniform("uSpeed", frostSpeed)
        }
    }

    Image(
        painter = BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .then(gestureModifier)
            .onSizeChanged { composableSize = it.toSize() }
            .graphicsLayer {
                renderEffect?.let { this.renderEffect = it }
            }
    )
}


