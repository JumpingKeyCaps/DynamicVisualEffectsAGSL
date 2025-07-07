package com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.composition

import android.content.Context
import android.graphics.RuntimeShader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.viewmodel.HolographicViewModel

@Composable
fun HolographicEffectScreen(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    @RawRes shaderResId: Int,
    viewModel: HolographicViewModel = viewModel(),
    sensorManager: SensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
) {
    val context = LocalContext.current

    val shaderText = remember(shaderResId) {
        context.resources.openRawResource(shaderResId)
            .bufferedReader()
            .use { it.readText() }
    }

    val runtimeShader = remember(shaderText) {
        RuntimeShader(shaderText)
    }

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()

    // Observe uiState from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // SensorEventListener pour la rotation vector sensor
    val sensorEventListener = remember {
        object : SensorEventListener {
            private val alpha = 0.1f
            private var filteredTiltX = 0f
            private var filteredTiltY = 0f

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        val orientationValues = FloatArray(3)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                        SensorManager.getOrientation(rotationMatrix, orientationValues)

                        val tiltX = orientationValues[1]
                        val tiltY = orientationValues[2]

                        filteredTiltX = alpha * tiltX + (1 - alpha) * filteredTiltX
                        filteredTiltY = alpha * tiltY + (1 - alpha) * filteredTiltY

                        viewModel.updateTilt(filteredTiltX, filteredTiltY)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(sensorManager) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Passer les uniforms depuis uiState
        runtimeShader.setFloatUniform("resolution", canvasWidth, canvasHeight)
        runtimeShader.setFloatUniform("imageSize", imageWidth, imageHeight)
        runtimeShader.setFloatUniform("tiltX", uiState.tiltX)
        runtimeShader.setFloatUniform("tiltY", uiState.tiltY)
        runtimeShader.setFloatUniform("time", uiState.time)
        runtimeShader.setFloatUniform("pulse", uiState.pulse)

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().shader = runtimeShader
            }
            canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, paint)
        }
    }
}