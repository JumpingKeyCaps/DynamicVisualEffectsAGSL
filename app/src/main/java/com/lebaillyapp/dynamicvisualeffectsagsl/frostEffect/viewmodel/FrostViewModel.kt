package com.lebaillyapp.dynamicvisualeffectsagsl.frostEffect.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

/**
 * Data class representing a single point of frost.
 * @param origin The (x, y) coordinates on the screen where the frost starts.
 * @param startTime The time in seconds when this frost point was created.
 * @param initialIntensity The initial visual intensity of the frost effect for this point.
 * @param propagationSpeed The speed at which the frost radius expands from this point (pixels/second).
 */
data class FrostPoint(
    val origin: Offset,
    val startTime: Float,
    val initialIntensity: Float, // Corresponds to uFrostIntensity in your shader
    val propagationSpeed: Float  // Corresponds to uSpeed in your shader
)

/**
 * Data class to hold all parameters needed by the AGSL shader for frost points.
 */
data class FrostShaderParams(
    val numFrostPoints: Int,
    val origins: FloatArray,      // Array of (x,y) positions
    val startTimes: FloatArray,   // Array of start times for each point
    val intensities: FloatArray,  // Array of current intensities for each point
    val speeds: FloatArray,       // Array of propagation speeds for each point
    val globalDecayRate: Float,   // New uniform for decay over time
    val minEffectThreshold: Float // Minimum intensity for a frost point to have an effect
) {
    // Override equals and hashCode for proper comparison of FloatArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FrostShaderParams

        if (numFrostPoints != other.numFrostPoints) return false
        if (!origins.contentEquals(other.origins)) return false
        if (!startTimes.contentEquals(other.startTimes)) return false
        if (!intensities.contentEquals(other.intensities)) return false
        if (!speeds.contentEquals(other.speeds)) return false
        if (globalDecayRate != other.globalDecayRate) return false
        if (minEffectThreshold != other.minEffectThreshold) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numFrostPoints
        result = 31 * result + origins.contentHashCode()
        result = 31 * result + startTimes.contentHashCode()
        result = 31 * result + intensities.contentHashCode()
        result = 31 * result + speeds.contentHashCode()
        result = 31 * result + globalDecayRate.hashCode()
        result = 31 * result + minEffectThreshold.hashCode()
        return result
    }
}


class FrostViewModel : ViewModel() {

    /** Maximum number of frost points the shader can handle simultaneously. */
    private val MAX_FROST_POINTS = 20 // Correspond au MAX_WAVES du ViewModel de l'eau

    /** Rate at which the intensity of frost points decays over time. */
    private val frostDecayRate = 0.75f // Taux de décroissance de l'intensité (similaire au damping)

    /** Minimum intensity below which a frost point is removed from the list. */
    private val minCpuIntensityThreshold = 0.05f // Quand l'intensité est trop faible, on nettoie en CPU

    /** Minimum intensity below which a frost point has no visual effect in the shader. */
    private val minShaderIntensityThreshold = 0.1f // Quand l'intensité est trop faible, le shader l'ignore

    /** Cooldown period in seconds between adding new frost points from the same pointer. */
    private val emissionCooldownSeconds = 0.1f // Pour éviter trop de points trop rapidement

    /** Minimum distance in pixels a pointer must move to add a new frost point. */
    private val minMovementDistance = 25f // Pour ajouter un point lors d'un glisser/traîner

    /** Initial intensity for a newly created frost point. */
    private val initialFrostIntensity = 1.0f

    /** Initial propagation speed for a newly created frost point (pixels/second for the radius). */
    private val initialFrostSpeed = 100f // Vitesse de propagation du givre

    /** MutableStateFlow holding the current list of active frost points. */
    private val _frostPoints = MutableStateFlow<List<FrostPoint>>(emptyList())
    val frostPoints: StateFlow<List<FrostPoint>> = _frostPoints

    /** Tracks last emission time per pointerId for cooldown control. */
    private val lastEmissionMap = mutableMapOf<Int, Float>()

    /** Tracks last positions per pointerId for distance-based emission control. */
    private val lastPositions = mutableMapOf<Int, Offset>()

    /**
     * Removes frost points whose calculated intensity falls below a threshold.
     * Should be called regularly with the current system time.
     *
     * @param currentTimeSeconds current system time in seconds.
     */
    fun cleanupFrostPoints(currentTimeSeconds: Float) {
        _frostPoints.value = _frostPoints.value.filter { point ->
            val elapsedSeconds = currentTimeSeconds - point.startTime
            val currentIntensity = point.initialIntensity * frostDecayRate.pow(elapsedSeconds)
            currentIntensity >= minCpuIntensityThreshold
        }
        Log.d("FrostViewModel", "Cleanup: ${ _frostPoints.value.size } active frost points.")
    }

    /**
     * Adds a new frost point at [position] if cooldown and movement distance conditions are met.
     * If MAX_FROST_POINTS limit is reached, the weakest (lowest intensity) point is removed first.
     *
     * @param position origin of the frost point in screen coordinates.
     * @param pointerId identifier of the pointer (finger) creating the point.
     * @param currentTimeSeconds current system time in seconds.
     */
    fun addFrostPoint(position: Offset, pointerId: Int, currentTimeSeconds: Float) {
        val lastEmission = lastEmissionMap[pointerId] ?: 0f
        val lastPos = lastPositions[pointerId]

        // Check cooldown and minimum movement distance
        if (currentTimeSeconds - lastEmission >= emissionCooldownSeconds &&
            (lastPos == null || (position - lastPos).getDistance() > minMovementDistance)
        ) {
            val newFrostPoint = FrostPoint(
                origin = position,
                startTime = currentTimeSeconds,
                initialIntensity = initialFrostIntensity,
                propagationSpeed = initialFrostSpeed
            )

            val currentPoints = _frostPoints.value.toMutableList()

            // If we've reached the maximum limit, remove the point with the lowest current intensity
            if (currentPoints.size >= MAX_FROST_POINTS) {
                val weakestPoint = currentPoints.minByOrNull { point ->
                    val elapsedSeconds = currentTimeSeconds - point.startTime
                    point.initialIntensity * frostDecayRate.pow(elapsedSeconds)
                }
                if (weakestPoint != null) {
                    currentPoints.remove(weakestPoint)
                    Log.d("FrostViewModel", "Removed weakest frost point at ${weakestPoint.origin}")
                }
            }

            currentPoints.add(newFrostPoint)
            _frostPoints.value = currentPoints

            lastEmissionMap[pointerId] = currentTimeSeconds
            lastPositions[pointerId] = position

            Log.d("FrostViewModel", "Frost point added at (${position.x}, ${position.y}) at time $currentTimeSeconds. Total points: ${currentPoints.size}")
        }
    }

    /**
     * Prepares frost point parameters for shader uniforms based on active points.
     *
     * @param currentTimeSeconds current system time in seconds.
     * @return [FrostShaderParams] containing uniform data arrays and counts.
     */
    fun getShaderUniforms(currentTimeSeconds: Float): FrostShaderParams {
        val activePoints = _frostPoints.value
        val numActualPoints = activePoints.size.coerceAtMost(MAX_FROST_POINTS)

        val origins = FloatArray(MAX_FROST_POINTS * 2) // x, y for each point
        val startTimes = FloatArray(MAX_FROST_POINTS)
        val intensities = FloatArray(MAX_FROST_POINTS)
        val speeds = FloatArray(MAX_FROST_POINTS)

        activePoints.take(MAX_FROST_POINTS).forEachIndexed { index, point ->
            val elapsedSeconds = currentTimeSeconds - point.startTime
            val currentIntensity = point.initialIntensity * frostDecayRate.pow(elapsedSeconds)

            origins[index * 2] = point.origin.x
            origins[index * 2 + 1] = point.origin.y
            startTimes[index] = point.startTime
            intensities[index] = currentIntensity
            speeds[index] = point.propagationSpeed
        }

        return FrostShaderParams(
            numFrostPoints = numActualPoints,
            origins = origins,
            startTimes = startTimes,
            intensities = intensities,
            speeds = speeds,
            globalDecayRate = frostDecayRate,
            minEffectThreshold = minShaderIntensityThreshold
        )
    }
}