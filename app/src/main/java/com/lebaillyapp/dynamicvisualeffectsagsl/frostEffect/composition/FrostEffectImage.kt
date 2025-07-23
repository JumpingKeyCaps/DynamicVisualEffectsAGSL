import android.graphics.RenderEffect // CELLE-CI EST CORRECTE, COMME DANS TON CODE ORIGINAL
import android.graphics.RuntimeShader
import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.lebaillyapp.dynamicvisualeffectsagsl.frostEffect.viewmodel.FrostViewModel


@Composable
fun FrostEffectImage(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,
    frostViewModel: FrostViewModel = viewModel()
) {
    val context = LocalContext.current

    val shaderCode = remember {
        context.resources.openRawResource(shaderResId)
            .bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }
    var composableSize by remember { mutableStateOf(Size.Zero) }

    var currentTimeSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime() // Correction: System.nanoTime() est la bonne fonction
        while (true) {
            withFrameNanos { frameTime ->
                currentTimeSeconds = (frameTime - startTime) / 1_000_000_000f
            }
            frostViewModel.cleanupFrostPoints(currentTimeSeconds)
            kotlinx.coroutines.delay(16)
        }
    }

    val currentShaderParams = frostViewModel.getShaderUniforms(currentTimeSeconds)

    val renderEffect = remember(shader, composableSize, currentShaderParams) {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            // Ici, RenderEffect est android.graphics.RenderEffect, et l'extension createRuntimeShaderEffect est bien importée
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader")
                .asComposeRenderEffect()
        } else null
    }

    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)
            shader.setFloatUniform("uTime", currentTimeSeconds)

            shader.setIntUniform("uNumFrostPoints", currentShaderParams.numFrostPoints)
            shader.setFloatUniform("uFrostPointOrigins", currentShaderParams.origins)
            shader.setFloatUniform("uFrostPointStartTimes", currentShaderParams.startTimes)
            shader.setFloatUniform("uFrostPointIntensities", currentShaderParams.intensities)
            shader.setFloatUniform("uFrostPointSpeeds", currentShaderParams.speeds)
            shader.setFloatUniform("uGlobalFrostDecayRate", currentShaderParams.globalDecayRate)
            shader.setFloatUniform("uMinEffectThreshold", currentShaderParams.minEffectThreshold)
        }
    }

    val touchModifier = Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                var event = awaitPointerEvent()
                event.changes.forEach { change ->
                    if (change.pressed) {
                        frostViewModel.addFrostPoint(change.position, change.id.value.toInt(), currentTimeSeconds)
                    }
                }
                while (event.changes.any { it.pressed }) {
                    event = awaitPointerEvent()
                    event.changes.forEach { change ->
                        if (change.pressed) {
                            frostViewModel.addFrostPoint(change.position, change.id.value.toInt(), currentTimeSeconds)
                        }
                    }
                }
            }
        }
    }

    Image(
        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap), // Préciser le package pour éviter les ambiguïtés
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { composableSize = it.toSize() }
            .then(touchModifier)
            .graphicsLayer {
                renderEffect?.let { this.renderEffect = it }
            }
    )
}