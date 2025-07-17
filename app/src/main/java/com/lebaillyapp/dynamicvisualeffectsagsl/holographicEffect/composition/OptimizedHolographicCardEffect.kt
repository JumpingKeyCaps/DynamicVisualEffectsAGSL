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
import kotlin.math.PI

@Composable
fun OptimizedHolographicCardEffect(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,

    // === PARAMÈTRES D'ENTRÉE CONTROLLABLES EXTERNEMENT ===
    hologramStrength: Float = 1.5f,                 // Opacité de l’effet holographique (sur les zones sombres)
    iridescentDarknessThreshold: Float = 0.001f,    // Seuil de luminosité en-dessous duquel l'effet s'active
    chromaticAberrationStrength: Float = 1.5f,      // Force du décalage RVB sur le roll

    effectIntensity: Float = 2.0f,                  // Multiplicateur final de l’effet arc-en-ciel
    fresnelPower: Float = 1.0f,                     // Puissance du falloff directionnel (style "reflet")
    rainbowScale: Float = 5.2f,                     // Taille des "taches d’huile" colorées
    rainbowOffset: Float = 1.0f,                    // Décalage global du motif rainbow
    normalStrength: Float = 158.5f,                   // Force du relief (impacte la déformation du pattern)
    microDetailScale: Float = 5.0f                 // Taille des détails dans le relief (finesse)
) {
    val context = LocalContext.current

    // === CAPTEURS ===
    var tiltRoll by remember { mutableFloatStateOf(0f) }
    var tiltPitch by remember { mutableFloatStateOf(0f) }
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val alpha = 0.15f // Lissage du signal des capteurs

    // === LISTENER ORIENTATION ===
    val sensorEventListener = rememberUpdatedState(object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                val rotationMatrix = FloatArray(9)
                val orientationValues = FloatArray(3)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationValues)

                val newRoll = (orientationValues[2] / PI.toFloat()).coerceIn(-1f, 1f)
                val newPitch = (orientationValues[1] / PI.toFloat()).coerceIn(-1f, 1f)

                tiltRoll = alpha * newRoll + (1 - alpha) * tiltRoll
                tiltPitch = alpha * newPitch + (1 - alpha) * tiltPitch
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    })

    // === ENREGISTREMENT DU CAPTEUR ===
    DisposableEffect(sensorManager) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationSensor?.let {
            sensorManager.registerListener(
                sensorEventListener.value,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
        }
    }

    // === LECTURE DU SHADER CODE ===
    val shaderCode = remember {
        context.resources.openRawResource(shaderResId)
            .bufferedReader().use { it.readText() }
    }
    val shader = remember { RuntimeShader(shaderCode) }

    // === MESURE DU COMPOSANT POUR SET uResolution ===
    var composableSize by remember { mutableStateOf(Size.Zero) }

    // === CRÉATION DU RENDER EFFECT ===
    val renderEffect = remember(
        shader, tiltRoll, tiltPitch,
        hologramStrength, iridescentDarknessThreshold, chromaticAberrationStrength,
        effectIntensity, fresnelPower, rainbowScale, rainbowOffset,
        normalStrength, microDetailScale
    ) {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            RenderEffect.createRuntimeShaderEffect(shader, "inputShader")
                .asComposeRenderEffect()
        } else null
    }

    // === SET DES UNIFORMS ===
    SideEffect {
        if (composableSize.width > 0f && composableSize.height > 0f) {
            // Base
            shader.setFloatUniform("uResolution", composableSize.width, composableSize.height)
            shader.setFloatUniform("uAspectRatio", composableSize.width / composableSize.height)

            // Capteurs
            shader.setFloatUniform("uTiltRoll", tiltRoll)
            shader.setFloatUniform("uTiltPitch", tiltPitch)

            // Paramètres de masquage / effets visuels
            shader.setFloatUniform("uHologramStrength", hologramStrength)
            shader.setFloatUniform("uIridescentDarknessThreshold", iridescentDarknessThreshold)
            shader.setFloatUniform("uChromaticAberrationStrength", chromaticAberrationStrength)

            // Paramètres du pattern rainbow + relief
            shader.setFloatUniform("uEffectIntensity", effectIntensity)
            shader.setFloatUniform("uFresnelPower", fresnelPower)
            shader.setFloatUniform("uRainbowScale", rainbowScale)
            shader.setFloatUniform("uRainbowOffset", rainbowOffset)
            shader.setFloatUniform("uNormalStrength", normalStrength)
            shader.setFloatUniform("uMicroDetailScale", microDetailScale)


        }
    }

    // === COMPOSITION VISUELLE ===
    Image(
        painter = BitmapPainter(bitmap),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { composableSize = it.toSize() }
            .then(
                if (renderEffect != null)
                    Modifier.graphicsLayer(renderEffect = renderEffect)
                else Modifier
            )
    )
}
