package com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.model

data class WaveShaderParams(
    val numWaves: Int,
    val origins: FloatArray,
    val amplitudes: FloatArray,
    val frequencies: FloatArray,
    val speeds: FloatArray,
    val startTimes: FloatArray,
    val globalDamping: Float,
    val minAmplitudeThreshold: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaveShaderParams) return false
        if (numWaves != other.numWaves) return false
        if (!origins.contentEquals(other.origins)) return false
        if (!amplitudes.contentEquals(other.amplitudes)) return false
        if (!frequencies.contentEquals(other.frequencies)) return false
        if (!speeds.contentEquals(other.speeds)) return false
        if (!startTimes.contentEquals(other.startTimes)) return false
        if (globalDamping != other.globalDamping) return false
        if (minAmplitudeThreshold != other.minAmplitudeThreshold) return false
        return true
    }

    override fun hashCode(): Int {
        var result = numWaves
        result = 31 * result + origins.contentHashCode()
        result = 31 * result + amplitudes.contentHashCode()
        result = 31 * result + frequencies.contentHashCode()
        result = 31 * result + speeds.contentHashCode()
        result = 31 * result + startTimes.contentHashCode()
        result = 31 * result + globalDamping.hashCode()
        result = 31 * result + minAmplitudeThreshold.hashCode()
        return result
    }
}