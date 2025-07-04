package com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.model.Wave
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.model.WaveShaderParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

/**
 * Water ViewModel to manage active waves state for AGSL shader deformation of the water effect.
 * Handles wave lifecycle, creation with cooldown, cleanup, and provides
 * uniform data formatted for the shader.
 */
class WaterViewModel : ViewModel() {


    /** Global damping factor controlling amplitude decay speed */
    private val damping = 0.55f // Changé de 0.3f à 0.95f pour moins de damping

    /** Minimum amplitude below which waves are removed from list */
    private val minValueToRemoveWave = 0.3f

    /** Minimum amplitude threshold below which waves do not deform pixels */
    private val minAmplitudeThresholdForShader = 2f

    /** MutableStateFlow holding the current list of active waves */
    private val _waves = MutableStateFlow<List<Wave>>(emptyList())
    val waves: StateFlow<List<Wave>> = _waves

    /** Tracks last emission time per pointerId for cooldown control */
    private val lastEmissionMap = mutableMapOf<Int, Float>()

    /** Cooldown in seconds between two waves emitted by same pointer */
    private val emissionCooldownSeconds = 0.05f // 50ms en secondes

    /** Initial amplitude for newly created waves */
    private val initialAmplitude = 80f

    /** Initial frequency for newly created waves */
    private val initialFrequency = 3f

    /** Initial speed for newly created waves (pixels per second) */
    private val initialSpeed = 350f//400f

    /** Tracks last positions per pointerId for distance-based emission control */
    private val lastPositions = mutableMapOf<Int, Offset>()

    /**
     * Removes waves whose amplitude after damping falls below the CPU threshold.
     * Should be called regularly with current system time.
     *
     * @param currentTimeSeconds current system time in seconds
     */
    fun cleanupWaves(currentTimeSeconds: Float) {
        _waves.value = _waves.value.filter { wave ->
            val elapsedSeconds = currentTimeSeconds - wave.startTime
            val currentAmplitude = wave.amplitude * damping.pow(elapsedSeconds)
            currentAmplitude >= minValueToRemoveWave
        }
    }

    /**
     * Adds a new wave at [position] if cooldown period for [pointerId] has elapsed.
     * If max waves limit is reached, removes the weakest wave first.
     *
     * @param position origin of the wave in screen coordinates
     * @param pointerId identifier of the pointer (finger)
     * @param currentTimeSeconds current system time in seconds
     */
    fun addWave(position: Offset, pointerId: Int, currentTimeSeconds: Float) {
        val lastEmission = lastEmissionMap[pointerId] ?: 0f
        val lastPos = lastPositions[pointerId]
        val minDistance = 15f // px

        if (currentTimeSeconds - lastEmission >= emissionCooldownSeconds &&
            (lastPos == null || (position - lastPos).getDistance() > minDistance)
        ) {
            val newWave = Wave(
                origin = position,
                startTime = currentTimeSeconds,
                amplitude = initialAmplitude,
                frequency = initialFrequency,
                speed = initialSpeed
            )

            val currentWaves = _waves.value.toMutableList()

            // Si on a atteint la limite, on retire l'onde la plus faible
            if (currentWaves.size >= 20) {
                val weakestWave = currentWaves.minByOrNull { wave ->
                    val elapsedSeconds = currentTimeSeconds - wave.startTime
                    wave.amplitude * damping.pow(elapsedSeconds)
                }
                if (weakestWave != null) {
                    currentWaves.remove(weakestWave)
                    Log.d("WaveDebug", "Removed weakest wave from time ${weakestWave.startTime}")
                }
            }

            currentWaves.add(newWave)
            _waves.value = currentWaves

            lastEmissionMap[pointerId] = currentTimeSeconds
            lastPositions[pointerId] = position

            Log.d("WaveDebug", "Wave added at (${position.x}, ${position.y}) at time $currentTimeSeconds. Total waves: ${currentWaves.size}")
        }
    }

    /**
     * Prepares wave parameters for shader uniforms based on active waves.
     *
     * @param currentTimeSeconds current system time in seconds
     * @return [WaveShaderParams] containing uniform data arrays and counts
     */
    fun getShaderUniforms(currentTimeSeconds: Float): WaveShaderParams {
        val activeWaves = _waves.value
        val maxWaves = 20 // Should match AGSL shader MAX_WAVES

        val origins = FloatArray(maxWaves * 2)
        val amplitudes = FloatArray(maxWaves)
        val frequencies = FloatArray(maxWaves)
        val speeds = FloatArray(maxWaves)
        val startTimes = FloatArray(maxWaves)

        activeWaves.take(maxWaves).forEachIndexed { index, wave ->
            val elapsedSeconds = currentTimeSeconds - wave.startTime
            val currentAmplitude = wave.amplitude * damping.pow(elapsedSeconds)

            origins[index * 2] = wave.origin.x
            origins[index * 2 + 1] = wave.origin.y
            amplitudes[index] = currentAmplitude
            frequencies[index] = wave.frequency
            speeds[index] = wave.speed
            startTimes[index] = wave.startTime
        }

        return WaveShaderParams(
            numWaves = activeWaves.size.coerceAtMost(maxWaves),
            origins = origins,
            amplitudes = amplitudes,
            frequencies = frequencies,
            speeds = speeds,
            startTimes = startTimes,
            globalDamping = damping,
            minAmplitudeThreshold = minAmplitudeThresholdForShader
        )
    }
}