package com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.model.HoloUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HolographicViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HoloUiState())
    val uiState: StateFlow<HoloUiState> = _uiState.asStateFlow()

    private var startTime = System.nanoTime()

    init {
        viewModelScope.launch {
            while (isActive) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000f
                _uiState.update { it.copy(time = elapsed) }
                delay(16L) // ~60fps
            }
        }
    }

    fun updateTilt(tiltX: Float, tiltY: Float) {
        _uiState.update {
            it.copy(tiltX = tiltX, tiltY = tiltY)
        }
    }

    fun triggerPulse() {
        // Simple pulse on/off pour l’instant
        viewModelScope.launch {
            _uiState.update { it.copy(pulse = 1f) }
            delay(300)
            _uiState.update { it.copy(pulse = 0f) }
        }
    }
}