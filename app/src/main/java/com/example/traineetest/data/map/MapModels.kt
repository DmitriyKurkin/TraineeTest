package com.example.traineetest.data.map


data class MapService(
    val id: String,
    val displayName: String
)

data class MapPin(
    val id: Int,
    val serviceId: String,
    val latitude: Double,
    val longitude: Double
)

data class MapPinsData(
    val services: List<MapService>,
    val pins: List<MapPin>
)