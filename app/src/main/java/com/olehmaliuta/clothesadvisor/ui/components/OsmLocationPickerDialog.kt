package com.olehmaliuta.clothesadvisor.ui.components

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

@Composable
fun OsmLocationPickerDialog(
    isOpen: Boolean,
    initialLocation: GeoPoint = GeoPoint(0.0, 0.0),
    onLocationSelected: (GeoPoint, String?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) {
        return
    }

    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var address by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(Modifier.fillMaxSize()) {
                OSMMapView(
                    context = context,
                    initialLocation = initialLocation,
                    onLocationSelected = { geoPoint ->
                        selectedLocation = geoPoint
                        scope.launch {
                            isLoading = true
                            address = getAddressFromGeoPoint(context, geoPoint)
                            isLoading = false
                        }
                    }
                )
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                address?.let {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                val finalAddress = address ?:
                                getAddressFromGeoPoint(context, selectedLocation)
                                onLocationSelected(selectedLocation, finalAddress)
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

@Composable
private fun OSMMapView(
    context: Context,
    initialLocation: GeoPoint,
    onLocationSelected: (GeoPoint) -> Unit
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(false)

                controller.setZoom(4.0)
                controller.setCenter(initialLocation)

                val marker = Marker(this).apply {
                    position = initialLocation
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }

                overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { tappedPoint ->
                            marker.position = tappedPoint
                            marker.setVisible(true)

                            onLocationSelected(tappedPoint)
                            controller.animateTo(tappedPoint)
                            invalidate()
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean = false
                }))

                overlays.add(marker)

                val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                myLocationOverlay.enableMyLocation()
                overlays.add(myLocationOverlay)

                mapView = this
            }
        },
        update = { view ->
            view.overlays.filterIsInstance<Marker>().firstOrNull()?.position = initialLocation
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}

private fun getAddressFromGeoPoint(context: Context, geoPoint: GeoPoint): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (!Geocoder.isPresent()) return null

        val addresses = geocoder.getFromLocation(
            geoPoint.latitude,
            geoPoint.longitude,
            1
        ) ?: return null

        addresses.firstOrNull()?.let { address ->
            buildString {
                for (i in 0..address.maxAddressLineIndex) {
                    append(address.getAddressLine(i))
                    if (i != address.maxAddressLineIndex) append(", ")
                }
            }
        }
    } catch (_: Exception) {
        null
    }
}