package com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lebaillyapp.dynamicvisualeffectsagsl.holographicEffect.model.HoloUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

class EnhancedHolographicViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HoloUiState())
    val uiState: StateFlow<HoloUiState> = _uiState.asStateFlow()

    private var startTime = System.nanoTime()
    private var pulseJob: Job? = null

    init {
        viewModelScope.launch {
            while (isActive) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000f
                _uiState.update { it.copy(time = elapsed) }
                Log.d("TimeUpdate", "Time: $elapsed")
                delay(16L) // ~60fps
            }
        }
    }

    fun updateTilt(tiltX: Float, tiltY: Float) {
        _uiState.update {
            it.copy(
                tiltX = tiltX,
                tiltY = tiltY
            )
        }
        Log.d("TiltUpdate", "TiltX: $tiltX, TiltY: $tiltY")

    }

    fun triggerPulse() {
        // Cancel any ongoing pulse
        pulseJob?.cancel()

        pulseJob = viewModelScope.launch {
            // Smooth pulse animation over 500ms
            val pulseDuration = 500L
            val startTime = System.currentTimeMillis()

            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = elapsed.toFloat() / pulseDuration.toFloat()

                if (progress >= 1f) {
                    _uiState.update { it.copy(pulse = 0f) }
                    break
                }

                // Smooth easing: start fast, slow down
                val easedProgress = 1f - (1f - progress) * (1f - progress)
                val pulseValue = max(0f, 1f - easedProgress)

                _uiState.update { it.copy(pulse = pulseValue) }
                delay(16L) // ~60fps
                Log.d("PulseUpdate", "Pulse: $pulseValue")

            }
        }
    }

    // Optional: for localized touch effects
    fun updateTouchPosition(x: Float, y: Float, containerSize: androidx.compose.ui.geometry.Size) {
        if (containerSize.width > 0f && containerSize.height > 0f) {
            // Convert to normalized coordinates (-0.5 to 0.5)
            val normalizedX = (x / containerSize.width - 0.5f) * containerSize.width / containerSize.height
            val normalizedY = y / containerSize.height - 0.5f

            _uiState.update {
                it.copy(
                    touchX = normalizedX,
                    touchY = normalizedY
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pulseJob?.cancel()
    }
}

