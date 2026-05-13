package com.example.traineetest.viewmodel


import com.example.traineetest.data.map.MapPin
import com.example.traineetest.data.map.MapService

sealed interface MapUiState {
    data object Loading : MapUiState

    data class Content(
        val services: List<MapService>,
        val selectedServiceIds: Set<String>,
        val pins: List<MapPin>,
        val totalPinsCount: Int
    ) : MapUiState

    data class Error(
        val message: String
    ) : MapUiState
}