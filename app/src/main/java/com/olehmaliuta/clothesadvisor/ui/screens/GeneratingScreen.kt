package com.olehmaliuta.clothesadvisor.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.requests.RecommendationRequest
import com.olehmaliuta.clothesadvisor.data.http.responses.GeneratedItemResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.GeneratedOutfitResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.WeatherResponse
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.types.PaletteInfo
import com.olehmaliuta.clothesadvisor.ui.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.ui.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.ui.components.ColorPicker
import com.olehmaliuta.clothesadvisor.ui.components.DateTimePicker
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.ui.components.OsmLocationPickerDialog
import com.olehmaliuta.clothesadvisor.ui.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.RecommendationViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.StorageViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GeneratingScreen(
    router: Router,
    authViewModel: AuthViewModel,
    storageViewModel: StorageViewModel,
    recommendationViewModel: RecommendationViewModel
) {
    when (authViewModel.authState.value) {
        is AuthState.Authenticated -> {
            ContentForUser(
                router = router,
                storageViewModel = storageViewModel,
                recommendationViewModel = recommendationViewModel
            )
        }
        AuthState.Unauthenticated -> {
            ContentForGuest(
                router = router
            )
        }
        else -> {}
    }
}

@Composable
private fun ContentForUser(
    router: Router,
    storageViewModel: StorageViewModel,
    recommendationViewModel: RecommendationViewModel
) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE)
            as LocationManager
    val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        Manifest.permission.ACCESS_COARSE_LOCATION
    else
        Manifest.permission.ACCESS_FINE_LOCATION

    var useCurrentLocation by remember { mutableStateOf(true) }
    var location by remember { mutableStateOf<GeoPoint?>(null) }
    var address by remember { mutableStateOf<String?>(null) }

    var dateAndTime by remember { mutableStateOf(Date()) }

    var includeColor by remember { mutableStateOf(true) }
    var color by remember { mutableStateOf(Color.Black) }

    var includePalettes by remember { mutableStateOf(true) }
    var palettes by remember { mutableStateOf(emptySet<String>()) }

    var eventType by remember { mutableStateOf<String?>(null) }
    var considerFavorites by remember { mutableStateOf(true) }

    var weatherResult by remember { mutableStateOf<WeatherResponse?>(null) }
    var generatedResults by remember {
        mutableStateOf<List<GeneratedOutfitResponse>?>(null)
    }

    var isEventTypeDropMenuOpen by remember { mutableStateOf(false) }
    var isLocationPickerOpen by remember { mutableStateOf(false) }
    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }

    var showRationale by remember { mutableStateOf(false) }
    var showPermanentDenial by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        requiredPermission
                    )) {
                    showRationale = true
                } else {
                    showPermanentDenial = true
                }
            }
        }
    )

    val errorMessageTitle = stringResource(R.string.generating__error_message_title)

    LaunchedEffect(recommendationViewModel.recommendationState) {
        when (val apiState = recommendationViewModel.recommendationState) {
            is ApiState.Success -> {
                weatherResult = apiState.data?.weather
                generatedResults = apiState.data?.outfits
            }
            is ApiState.Error -> {
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    InfoDialog(
        title = okDialogTitle,
        content = okDialogMessage,
        onConfirm = {
            okDialogMessage = null
        }
    )

    AcceptCancelDialog(
        isOpen = showRationale,
        title = stringResource(R.string.permissions__access_fine_location__title),
        onDismiss = { showRationale = false },
        onAccept = {
            showRationale = false
            permissionLauncher.launch(requiredPermission)
        },
        acceptText = stringResource(R.string.permissions__try_again_button),
    ) {
        Text(stringResource(R.string.permissions__reason__get_location))
    }

    AcceptCancelDialog(
        isOpen = showPermanentDenial,
        title = stringResource(R.string.permissions__permanent__required),
        onDismiss = { showPermanentDenial = false },
        onAccept = {
            showPermanentDenial = false
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
        acceptText = stringResource(R.string.permissions__open_settings_button),
    ) {
        Text(stringResource(R.string.permissions__access_fine_location__permanent))
    }

    OsmLocationPickerDialog(
        isOpen = isLocationPickerOpen,
        initialLocation = location ?: GeoPoint(0.0, 0.0),
        onLocationSelected = { geoPoint, selectedAddress ->
            location = geoPoint
            address = selectedAddress
            isLocationPickerOpen = false
        },
        onDismiss = { isLocationPickerOpen = false }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
            .testTag("main_content_container"),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = useCurrentLocation,
                onCheckedChange = { useCurrentLocation = !useCurrentLocation },
                modifier = Modifier
                    .testTag("use_device_location_switch")
                    .semantics {
                    this.contentDescription =
                        if (useCurrentLocation) "Checked" else "Unchecked"
                }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.generating__location__use_device_location),
                fontSize = 17.sp
            )
        }

        if (!useCurrentLocation) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
            ) {
                Button(
                    onClick = { isLocationPickerOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.generating__location__select_button))
                }

                if (location != null) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(
                            R.string.generating__location__latitude,
                            location!!.latitude),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.generating__location__longitude,
                            location!!.longitude),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    address?.let {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(
                                R.string.generating__location__address,
                                it),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        DateTimePicker(
            selectedDate = dateAndTime,
            onDateSelected = { newDate -> dateAndTime = newDate }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = includeColor,
                onCheckedChange = { includeColor = !includeColor },
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (includeColor) "Checked" else "Unchecked"
                }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.generating__color__include),
                fontSize = 17.sp
            )
        }

        if (includeColor) {
            Spacer(modifier = Modifier.height(10.dp))

            ColorPicker(
                color = color,
                imageUri = null,
                onColorChange = { newColor -> color = newColor }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = includePalettes,
                onCheckedChange = { includePalettes = !includePalettes },
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (includePalettes) "Checked" else "Unchecked"
                }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.generating__palettes__include),
                fontSize = 17.sp
            )
        }

        if (includePalettes) {
            Spacer(modifier = Modifier.height(10.dp))

            ColorPaletteSelector(
                selectedPalettes = palettes,
                onPaletteSelected = { selectedPalette ->
                    palettes = if (palettes.contains(selectedPalette)) {
                        palettes - selectedPalette
                    } else {
                        palettes + selectedPalette
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.generating__event__title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isEventTypeDropMenuOpen = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                    .fillMaxWidth()
            ) {
                Text(
                    text = eventType?.let {
                        stringResource(AppConstants.events.getValue(it))
                    } ?: "-",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    color = if (eventType == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )

                if (eventType != null) {
                    IconButton(
                        onClick = { eventType = null },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear event",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select event",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            DropdownMenu(
                expanded = isEventTypeDropMenuOpen,
                onDismissRequest = { isEventTypeDropMenuOpen = false }
            ) {
                AppConstants.events.forEach { eventOption ->
                    DropdownMenuItem(
                        text = { Text(stringResource(eventOption.value)) },
                        onClick = {
                            eventType = eventOption.key
                            isEventTypeDropMenuOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = considerFavorites,
                onCheckedChange = {
                    considerFavorites = !considerFavorites
                },
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (location == null) "Checked" else "Unchecked"
                }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.generating__favorites__label),
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        val locationNotFoundMessage = stringResource(R.string.generating__location_not_found_message)

        Button(
            enabled = if (!useCurrentLocation) location != null else true &&
                recommendationViewModel.recommendationState !is ApiState.Loading,
            onClick = {
                if (useCurrentLocation) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            requiredPermission
                        ) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(requiredPermission)
                        return@Button
                    }
                }

                val geoData = if (useCurrentLocation) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (currentLocation != null)
                            GeoPoint(currentLocation.latitude, currentLocation.longitude)
                        else {
                            okDialogTitle = errorMessageTitle
                            okDialogMessage = locationNotFoundMessage
                            return@Button
                        }
                    } else {
                        val geoResult = recommendationViewModel.getDeviceLocation(context)
                        if (geoResult != null) geoResult
                        else {
                            okDialogTitle = errorMessageTitle
                            okDialogMessage = locationNotFoundMessage
                            return@Button
                        }
                    }
                } else location!!

                val dateFormatter = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault())

                recommendationViewModel.recommendations(
                    context = context,
                    request = RecommendationRequest(
                        latitude = geoData.latitude,
                        longitude = geoData.longitude,
                        targetTime = dateFormatter.format(dateAndTime),
                        red = if (includeColor) (color.red * 255).toInt() else null,
                        green = if (includeColor) (color.green * 255).toInt() else null,
                        blue = if (includeColor) (color.blue * 255).toInt() else null,
                        paletteTypes = if (includePalettes) palettes.toList() else null,
                        event = eventType,
                        includeFavorites = considerFavorites
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generate_button")
        ) {
            Text(stringResource(R.string.generating__generate_button))
        }

        HorizontalDivider(
            thickness = 3.dp,
            modifier = Modifier.padding(vertical = 15.dp)
        )

        if (recommendationViewModel.recommendationState is ApiState.Loading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        } else {
            if (weatherResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("weather_info_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.generating__results__weather__temp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${weatherResult!!.temp}°C",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (weatherResult!!.code != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://openweathermap.org/img/wn/${weatherResult!!.icon}@2x.png")
                                            .memoryCachePolicy(CachePolicy.DISABLED)
                                            .diskCachePolicy(CachePolicy.DISABLED)
                                            .setHeader(
                                                "Cache-Control",
                                                "no-store, no-cache, must-revalidate")
                                            .setHeader("Pragma", "no-cache")
                                            .setHeader("Expires", "0")
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = weatherResult!!.code!!.toString(),
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = Color.Gray,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = stringResource(AppConstants.weatherTypes
                                            .getValue(weatherResult!!.code!!))
                                            .replaceFirstChar { it.titlecase() },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (generatedResults == null) {
                Text(
                    text = stringResource(R.string.generating__results__hint),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
            } else if (generatedResults!!.isEmpty()) {
                Text(
                    text = stringResource(R.string.generating__results__none),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
            } else {
                generatedResults?.forEach { outfitInfo ->
                    OutfitCard(
                        outfit = outfitInfo,
                        onSaveClick = {
                            storageViewModel.initialItemIds.value = outfitInfo.items
                                ?.filter { item -> item.id != null }
                                ?.map { item -> item.id!! }?.toSet() ?: emptySet()
                            router.navigate(
                                route = Screen.EditOutfit.name,
                                apiStatesToRestore = listOf(recommendationViewModel)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentForGuest(
    router: Router
) {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.generating__guest__title),
                textAlign = TextAlign.Center,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextButton(
                onClick = { router.navigate(Screen.LogIn.name) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("log_in__button")
            ) {
                Text(
                    text = stringResource(R.string.generating__guest__log_in_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { router.navigate(Screen.Registration.name) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_up__button")
            ) {
                Text(
                    text = stringResource(R.string.generating__guest__sign_up_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ColorPaletteSelector(
    selectedPalettes: Set<String>,
    onPaletteSelected: (String) -> Unit
) {
    var isSelectionMenuOpen by remember { mutableStateOf(false) }

    if (isSelectionMenuOpen) {
        AlertDialog(
            onDismissRequest = {
                isSelectionMenuOpen = false
            },
            title = { Text(stringResource(R.string.generating__palettes__dialog__title)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    AppConstants.palettes.forEach { palette ->
                        ColorPaletteSelectorCard(
                            key = palette.key,
                            palette = palette.value,
                            isChecked = selectedPalettes.contains(palette.key),
                            onClick = { onPaletteSelected(palette.key) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = { isSelectionMenuOpen = false }
                ) {
                    Text(stringResource(R.string.generating__palettes__dialog__done_button))
                }
            }
        )
    }

    Column {
        Text(
            text = stringResource(R.string.generating__palettes__title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = { isSelectionMenuOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Sort Direction",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (!selectedPalettes.isEmpty()) {
                selectedPalettes.forEach { key ->
                    SelectedColorPaletteElement(
                        AppConstants.palettes.getValue(key),
                        onRemove = { onPaletteSelected(key) }
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.generating__palettes__no_selected),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ColorPaletteSelectorCard(
    key: String,
    palette: PaletteInfo,
    isChecked: Boolean,
    onClick: (String) -> Unit
) {
    Card(
        onClick = { onClick(key) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(palette.nameId),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(palette.descriptionId),
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(palette.imageId),
                contentDescription = "Palette",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SelectedColorPaletteElement(
    palette: PaletteInfo,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        onClick = { onRemove() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(palette.nameId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove Selected Palette"
            )
        }
    }
}

@Composable
private fun OutfitCard(
    outfit: GeneratedOutfitResponse,
    onSaveClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .testTag("outfit_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            outfit.scoreAvg?.let { score ->
                val (ratingText, ratingColor) = when {
                    score >= 0.9 -> stringResource(R.string.outfit_generating_score__perfect_fit) to Color(0xFF4CAF50)
                    score >= 0.7 -> stringResource(R.string.outfit_generating_score__good_fit) to Color(0xFF8BC34A)
                    score >= 0.5 -> stringResource(R.string.outfit_generating_score__decent_fit) to Color(0xFFFFC107)
                    score >= 0.3 -> stringResource(R.string.outfit_generating_score__not_ideal) to Color(0xFFFF9800)
                    else -> stringResource(R.string.outfit_generating_score__poor_fit) to Color(0xFFF44336)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ratingColor.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = ratingText,
                            color = ratingColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = stringResource(
                            R.string.generating__results__outfit__score,
                            "%.1f".format(score * 100)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            outfit.paletteType?.let { palette ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.generating__results__outfit__palette),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(
                            AppConstants.palettes[palette]?.nameId ?:
                            R.string.palettes__none__name),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = stringResource(R.string.generating__results__outfit__items),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                outfit.items?.forEach { item ->
                    ClothingItemCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("outfit_card__save_button")
            ) {
                Text(stringResource(R.string.generating__results__outfit__save_button))
            }
        }
    }
}

@Composable
private fun ClothingItemCard(item: GeneratedItemResponse) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!item.image.isNullOrEmpty()) {
                val resultImageUrl = item.image!!.replace(
                    "://localhost", "://10.0.2.2")

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(resultImageUrl)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .setHeader(
                            "Cache-Control",
                            "no-store, no-cache, must-revalidate")
                        .setHeader("Pragma", "no-cache")
                        .setHeader("Expires", "0")
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(),
                    error = rememberVectorPainter(Icons.Default.Warning)
                )
            } else {
                Image(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "No image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.name ?: "Unknown",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        item.category?.let { category ->
            Text(
                text = stringResource(AppConstants.categories.getValue(category)),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}