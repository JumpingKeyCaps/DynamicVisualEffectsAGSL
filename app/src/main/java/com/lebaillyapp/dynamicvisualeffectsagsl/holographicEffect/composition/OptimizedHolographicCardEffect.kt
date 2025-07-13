package com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay
import kotlin.math.PI

@Composable
fun OptimizedHolographicCardEffect(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,

    // Paramètres principaux simplifiés pour le contrôle global de l'effet
    hologramStrength: Float = 1.0f, // Intensité globale de l'effet holographique
    iridescentPatternScale: Float = 50.0f, // Taille des motifs iridescents (plus grand = plus de détails)
    iridescentDarknessThreshold: Float = 0.2f, // Seuil de noirceur pour l'iridescence (0.0=noir pur, 1.0=blanc)
    chromaticAberrationStrength: Float = 0.005f, // Force de l'aberration chromatique (petite valeur, e.g., 0.001-0.01)
    reflectionRoughness: Float = 0.3f, // Rugosité du reflet (0.0=miroir, 1.0=mat)
    sparkleVisibility: Float = 0.8f, // Visibilité des paillettes
    animationSpeed: Float = 1.0f // Vitesse générale des animations (motifs, shimmer)
) {
    val context = LocalContext.current
    val TAG = "OptimizedHolographicCardEffect"

    var tiltRoll by remember { mutableFloatStateOf(0f) }
    var elapsedTime by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val alpha = 0.15f // Lissage des capteurs

    LaunchedEffect(animationSpeed) {
        val startTime = System.currentTimeMillis()
        while (true) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000f * animationSpeed
            delay(16) // Environ 60 FPS
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

                    // On se concentre uniquement sur le ROLL (orientationValues[2])
                    val newRoll = (orientationValues[2] / PI.toFloat()).coerceIn(-1.0f, 1.0f)
                    tiltRoll = alpha * newRoll + (1 - alpha) * tiltRoll

                    // Log.d(TAG, "Sensor update: Roll=${tiltRoll}") // Décommenter pour debug
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
            sensorManager.registerListener(
                sensorEventListener.value,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
            // Log.d(TAG, "Sensor listener unregistered.") // Décommenter pour debug
        }
    }

    val shaderCode = remember {
        context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }

    var composableSize by remember { mutableStateOf(Size.Zero) }

    val renderEffect = if (composableSize.width > 0f && composableSize.height > 0f) {
        remember(
            shader,
            tiltRoll, elapsedTime,
            hologramStrength, iridescentPatternScale, iridescentDarknessThreshold,
            chromaticAberrationStrength, reflectionRoughness, sparkleVisibility,
            animationSpeed
        ) { // uDebugStep a été retiré des dépendances
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader").asComposeRenderEffect()
        }
    } else {
        null
    }

    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            // Uniforms de base
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)
            shader.setFloatUniform("uTime", elapsedTime)

            // Capteur (ROLL uniquement)
            shader.setFloatUniform("uTiltRoll", tiltRoll)

            // Paramètres des effets
            shader.setFloatUniform("uHologramStrength", hologramStrength)
            shader.setFloatUniform("uIridescentPatternScale", iridescentPatternScale)
            shader.setFloatUniform("uIridescentDarknessThreshold", iridescentDarknessThreshold)
            shader.setFloatUniform("uChromaticAberrationStrength", chromaticAberrationStrength)
            shader.setFloatUniform("uReflectionRoughness", reflectionRoughness)
            shader.setFloatUniform("uSparkleVisibility", sparkleVisibility)
            shader.setFloatUniform("uAnimationSpeed", animationSpeed)
            // shader.setIntUniform("uDebugStep", debugStep) // uDebugStep a été retiré

            // Log.d(TAG, "All shader uniforms set successfully") // Décommenter pour debug
        }
    }

    Image(
        painter = BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                composableSize = size.toSize()
                // Log.d(TAG, "onSizeChanged triggered! New size: $composableSize") // Décommenter pour debug
            }
            .then(
                if (renderEffect != null) Modifier.graphicsLayer(renderEffect = renderEffect)
                else Modifier
            )
    )
}