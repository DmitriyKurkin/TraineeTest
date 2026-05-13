package com.example.traineetest.data.map


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MapPinsRepository(
    private val context: Context
) {

    suspend fun getPinsData(): Result<MapPinsData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val rawJson = context.assets
                    .open("pins.json")
                    .bufferedReader()
                    .use { it.readText() }

                val root = JSONObject(rawJson)

                val declaredServices = buildSet {
                    val servicesArray = root.getJSONArray("services")

                    for (index in 0 until servicesArray.length()) {
                        add(servicesArray.getString(index))
                    }
                }

                val pinsArray = root.getJSONArray("pins")
                val pins = mutableListOf<MapPin>()
                val servicesFromPins = mutableSetOf<String>()

                for (index in 0 until pinsArray.length()) {
                    val pinObject = pinsArray.getJSONObject(index)
                    val coordinatesObject = pinObject.getJSONObject("coordinates")

                    val serviceId = pinObject.getString("service")
                    servicesFromPins += serviceId

                    pins += MapPin(
                        id = pinObject.getInt("id"),
                        serviceId = serviceId,
                        latitude = coordinatesObject.getDouble("lat"),
                        longitude = coordinatesObject.getDouble("lng")
                    )
                }

                val services = (declaredServices + servicesFromPins)
                    .sorted()
                    .map { serviceId ->
                        MapService(
                            id = serviceId,
                            displayName = "Сервис ${serviceId.uppercase()}"
                        )
                    }

                MapPinsData(
                    services = services,
                    pins = pins
                )
            }
        }
    }
}