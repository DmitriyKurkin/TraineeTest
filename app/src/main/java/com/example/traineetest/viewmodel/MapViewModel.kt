package com.example.traineetest.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.traineetest.data.map.MapPin
import com.example.traineetest.data.map.MapPinsRepository
import com.example.traineetest.data.map.MapService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: MapPinsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var allServices: List<MapService> = emptyList()
    private var allPins: List<MapPin> = emptyList()
    private var selectedServiceIds: Set<String> = emptySet()

    init {
        loadPins()
    }

    fun loadPins() {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading

            val result = repository.getPinsData()

            if (result.isSuccess) {
                val data = result.getOrNull()

                if (data == null) {
                    _uiState.value = MapUiState.Error("Не удалось прочитать pins.json")
                    return@launch
                }

                allServices = data.services
                allPins = data.pins
                selectedServiceIds = data.services.map { it.id }.toSet()

                reduceContent()
            } else {
                _uiState.value = MapUiState.Error("Не удалось загрузить точки карты")
            }
        }
    }

    fun onServiceCheckedChanged(
        serviceId: String,
        checked: Boolean
    ) {
        selectedServiceIds = if (checked) {
            selectedServiceIds + serviceId
        } else {
            selectedServiceIds - serviceId
        }

        reduceContent()
    }

    fun selectAllServices() {
        selectedServiceIds = allServices.map { it.id }.toSet()
        reduceContent()
    }

    fun clearAllServices() {
        selectedServiceIds = emptySet()
        reduceContent()
    }

    private fun reduceContent() {
        val visiblePins = allPins.filter { pin ->
            pin.serviceId in selectedServiceIds
        }

        _uiState.value = MapUiState.Content(
            services = allServices,
            selectedServiceIds = selectedServiceIds,
            pins = visiblePins,
            totalPinsCount = allPins.size
        )
    }
}

class MapViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                repository = MapPinsRepository(
                    context = context.applicationContext
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}