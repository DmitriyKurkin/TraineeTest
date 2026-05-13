package com.example.traineetest.ui.map


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traineetest.data.map.MapPin
import com.example.traineetest.data.map.MapService
import com.example.traineetest.viewmodel.MapUiState
import com.example.traineetest.viewmodel.MapViewModel
import com.example.traineetest.viewmodel.MapViewModelFactory
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(context)
    )

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карта") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = state is MapUiState.Content,
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтр сервисов"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            MapUiState.Loading -> {
                MapLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is MapUiState.Error -> {
                MapErrorScreen(
                    message = currentState.message,
                    onRetryClick = viewModel::loadPins,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is MapUiState.Content -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    OsmMap(
                        pins = currentState.pins,
                        modifier = Modifier.fillMaxSize()
                    )

                    MapCounter(
                        visiblePinsCount = currentState.pins.size,
                        totalPinsCount = currentState.totalPinsCount,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }

                if (showFilterSheet) {
                    MapFilterBottomSheet(
                        services = currentState.services,
                        selectedServiceIds = currentState.selectedServiceIds,
                        onCheckedChanged = viewModel::onServiceCheckedChanged,
                        onSelectAllClick = viewModel::selectAllServices,
                        onClearAllClick = viewModel::clearAllServices,
                        onDismiss = { showFilterSheet = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun MapLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MapErrorScreen(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onRetryClick) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Повторить")
        }
    }
}

@Composable
private fun MapCounter(
    visiblePinsCount: Int,
    totalPinsCount: Int,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = {}
    ) {
        Text("Показано $visiblePinsCount из $totalPinsCount")
    }
}

@Composable
private fun OsmMap(
    pins: List<MapPin>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(11.0)
            controller.setCenter(GeoPoint(55.751244, 37.618423))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        },
        update = { view ->
            view.overlays.clear()

            pins.forEach { pin ->
                val marker = Marker(view).apply {
                    position = GeoPoint(pin.latitude, pin.longitude)
                    title = "Сервис ${pin.serviceId.uppercase()}"
                    subDescription = "Точка #${pin.id}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }

                view.overlays.add(marker)
            }

            view.invalidate()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapFilterBottomSheet(
    services: List<MapService>,
    selectedServiceIds: Set<String>,
    onCheckedChanged: (serviceId: String, checked: Boolean) -> Unit,
    onSelectAllClick: () -> Unit,
    onClearAllClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Text(
            text = "Фильтр сервисов",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onSelectAllClick
            ) {
                Text("Выбрать все")
            }

            TextButton(
                onClick = onClearAllClick
            ) {
                Text("Сбросить")
            }
        }

        HorizontalDivider()

        services.forEach { service ->
            val checked = service.id in selectedServiceIds

            ListItem(
                modifier = Modifier.clickable {
                    onCheckedChanged(service.id, !checked)
                },
                headlineContent = {
                    Text(service.displayName)
                },
                supportingContent = {
                    Text("id: ${service.id}")
                },
                leadingContent = {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            onCheckedChanged(service.id, isChecked)
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}