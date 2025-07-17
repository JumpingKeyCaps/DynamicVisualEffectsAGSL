package com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition


import android.content.Context
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay
import kotlin.math.PI
import android.util.Log

@Composable
fun UltraRealisticHolographicEffectShader(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,

    // Effets principaux (simplifiés)
    effectIntensity: Float = 0.8f,
    fresnelPower: Float = 5.0f,

    // Rainbow et spectre (simplifiés)
    rainbowScale: Float = 2.5f,
    rainbowOffset: Float = 0.2f,

    // Normales et micro-relief (simplifiés)
    normalStrength: Float = 0.3f,
    microDetailScale: Float = 50.0f,
) {
    val context = LocalContext.current
    val TAG = "UltraRealisticHolographicShader"

    // États pour les capteurs
    var tiltPitch by remember { mutableFloatStateOf(0f) }
    var tiltRoll by remember { mutableFloatStateOf(0f) }

    // État pour l'animation temporelle
    var elapsedTime by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val alpha = 0.1f // Lissage des capteurs

    // Animation temporelle
    LaunchedEffect(Unit) { // animationSpeed n'est plus un paramètre, donc LaunchedEffect ne dépend plus de lui
        val startTime = System.currentTimeMillis()
        while (true) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000f // Pas de multiplication par animationSpeed
            delay(16) // ~60 FPS
        }
    }

    val sensorEventListener = rememberUpdatedState(object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    val orientationValues = FloatArray(3)

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)

                    // Ces valeurs sont déjà normalisées de -PI/2 à PI/2 et -PI à PI
                    // Le shader s'attend à des valeurs de l'ordre de 1.0, donc on les adapte.
                    // uTiltPitch * 2.0 et uTiltRoll * 3.0 dans le shader sont la raison de cette adaptation.
                    val newPitch = (orientationValues[1] / (PI.toFloat() / 2f)) // Normalise pitch à [-1, 1]
                    val newRoll = (orientationValues[2] / PI.toFloat()) // Normalise roll à [-1, 1]

                    tiltPitch = alpha * newPitch + (1 - alpha) * tiltPitch
                    tiltRoll = alpha * newRoll + (1 - alpha) * tiltRoll

                    // Clamp les valeurs lissées pour s'assurer qu'elles restent dans les bornes attendues par le shader si le lissage les sortait.
                    // Bien que le shader les coerce, il est bon de les garder propres ici aussi.
                    tiltPitch = tiltPitch.coerceIn(-1.0f, 1.0f)
                    tiltRoll = tiltRoll.coerceIn(-1.0f, 1.0f)

                    Log.d(TAG, "Sensor update: Pitch=${tiltPitch}, Roll=${tiltRoll}")
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    })

    DisposableEffect(sensorManager) {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationVectorSensor == null) {
            Log.e(TAG, "TYPE_ROTATION_VECTOR sensor NOT available on this device!")
        } else {
            Log.d(TAG, "TYPE_ROTATION_VECTOR sensor FOUND. Registering listener...")
            sensorManager.registerListener(
                sensorEventListener.value,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
            Log.d(TAG, "Sensor listener unregistered.")
        }
    }

    val shaderCode = remember {
        context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }

    var composableSize by remember { mutableStateOf(Size.Zero) }

    // RenderEffect avec les dépendances mises à jour
    val renderEffect = if (composableSize.width > 0f && composableSize.height > 0f) {
        remember(
            shader,
            tiltPitch, tiltRoll, elapsedTime,
            effectIntensity, fresnelPower,
            rainbowScale, rainbowOffset,
            normalStrength, microDetailScale
        ) {
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader").asComposeRenderEffect()
        }
    } else {
        null
    }

    // Configuration des uniforms
    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            // Uniforms de base
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)
            shader.setFloatUniform("uTime", elapsedTime)

            // Capteurs
            shader.setFloatUniform("uTiltPitch", tiltPitch)
            shader.setFloatUniform("uTiltRoll", tiltRoll)

            // Effets principaux
            shader.setFloatUniform("uEffectIntensity", effectIntensity)
            shader.setFloatUniform("uFresnelPower", fresnelPower)

            // Rainbow et spectre
            shader.setFloatUniform("uRainbowScale", rainbowScale)
            shader.setFloatUniform("uRainbowOffset", rainbowOffset)

            // Normales et micro-relief
            shader.setFloatUniform("uNormalStrength", normalStrength)
            shader.setFloatUniform("uMicroDetailScale", microDetailScale)

            Log.d(TAG, "All shader uniforms set successfully")
        }
    }

    Image(
        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                composableSize = size.toSize()
                Log.d(TAG, "onSizeChanged triggered! New size: $composableSize")
            }
            .then(
                if (renderEffect != null) Modifier.graphicsLayer(renderEffect = renderEffect)
                else Modifier
            )
    )
}