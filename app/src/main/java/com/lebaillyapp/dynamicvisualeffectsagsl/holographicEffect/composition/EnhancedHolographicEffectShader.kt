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
import kotlin.math.PI
import android.util.Log

@Composable
fun EnhancedHolographicEffectShader(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,

    effectIntensity: Float = 1.0f,
    shininess: Float = 100.0f,
    rainbowScale: Float = 1.0f,
    rainbowOffset: Float = 0.0f, // Make sure this is Float, not 0.0f
    patternDensity: Float = 20.0f,
    patternVisibility: Float = 0.5f,
    sparkleDensity: Float = 50.0f,
    sparklePower: Float = 8.0f,
    sparkleIntensity: Float = 0.5f,
    chromaticAberrationStrength: Float = 0.02f
) {
    val context = LocalContext.current
    val TAG = "HolographicShader"

    var tiltPitch by remember { mutableFloatStateOf(0f) }
    var tiltRoll by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val alpha = 0.1f

    val sensorEventListener = rememberUpdatedState(object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    val orientationValues = FloatArray(3)

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)

                    val newPitch = (orientationValues[1] / (PI.toFloat() / 2f)).coerceIn(-1.0f, 1.0f)
                    val newRoll = (orientationValues[2] / PI.toFloat()).coerceIn(-1.0f, 1.0f)

                    tiltPitch = alpha * newPitch + (1 - alpha) * tiltPitch
                    tiltRoll = alpha * newRoll + (1 - alpha) * tiltRoll

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

    // *** MODIFICATION HERE: Include dynamic uniforms in remember keys ***
    val renderEffect = if (composableSize.width > 0f && composableSize.height > 0f) {
        remember(
            shader, // Keep shader as a key
            tiltPitch, // Add tiltPitch as a key
            tiltRoll,  // Add tiltRoll as a key
            effectIntensity, shininess, rainbowScale, rainbowOffset, // Add all other dynamic uniforms as keys
            patternDensity, patternVisibility, sparkleDensity, sparklePower, sparkleIntensity,
            chromaticAberrationStrength
        ) {
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader").asComposeRenderEffect()
        }
    } else {
        null
    }

    // Set uniforms inside SideEffect (this part was already correct)
    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)

            shader.setFloatUniform("uTiltPitch", tiltPitch)
            shader.setFloatUniform("uTiltRoll", tiltRoll)

            shader.setFloatUniform("uEffectIntensity", effectIntensity)
            shader.setFloatUniform("uShininess", shininess)
            shader.setFloatUniform("uRainbowScale", rainbowScale)
            shader.setFloatUniform("uRainbowOffset", rainbowOffset)
            shader.setFloatUniform("uPatternDensity", patternDensity)
            shader.setFloatUniform("uPatternVisibility", patternVisibility)
            shader.setFloatUniform("uSparkleDensity", sparkleDensity)
            shader.setFloatUniform("uSparklePower", sparklePower)
            shader.setFloatUniform("uSparkleIntensity", sparkleIntensity)
            shader.setFloatUniform("uChromaticAberrationStrength", chromaticAberrationStrength)

            Log.d(TAG, "Shader uniforms set: TiltPitch=$tiltPitch, TiltRoll=$tiltRoll, Resolution=$composableSize")
        } else {
            Log.d(TAG, "Composable size not ready yet: $composableSize")
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