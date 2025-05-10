package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.types.PaletteInfo
import com.olehmaliuta.clothesadvisor.ui.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.ui.components.ColorPicker
import com.olehmaliuta.clothesadvisor.ui.components.DateTimePicker
import com.olehmaliuta.clothesadvisor.ui.navigation.Router
import com.olehmaliuta.clothesadvisor.ui.navigation.Screen
import com.olehmaliuta.clothesadvisor.ui.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants
import java.util.Date

@Composable
fun GeneratingScreen(
    router: Router,
    authViewModel: AuthViewModel,
) {
    when (authViewModel.authState.value) {
        is AuthState.Authenticated -> {
            ContentForUser()
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
private fun ContentForUser() {
    var dateAndTime by remember { mutableStateOf<Date>(Date()) }
    var mainColor by remember { mutableStateOf(Color.Black) }
    var palettes by remember { mutableStateOf(emptySet<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
            .testTag("main_content_container"),
    ) {
        DateTimePicker(
            selectedDate = dateAndTime,
            onDateSelected = { newDate -> dateAndTime = newDate }
        )

        Spacer(modifier = Modifier.height(30.dp))

        ColorPicker(
            color = mainColor,
            imageUri = null,
            onColorChange = { newColor -> mainColor = newColor }
        )

        Spacer(modifier = Modifier.height(30.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Generate outfits")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

        Text(
            text = "Generated results will be displayed here",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
        )
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
                text = "You need to log in to use the generating feature.",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    lineHeight = 35.sp)
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
                    text = "Log In",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
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
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
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
            title = { Text("Select Palettes") },
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
                    Text("Done")
                }
            }
        )
    }

    Column {
        Text(
            text = "Selected Palettes",
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

            selectedPalettes.forEach { key ->
                SelectedColorPaletteElement(
                    AppConstants.palettes.getValue(key),
                    onRemove = { onPaletteSelected(key) }
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
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = palette.name,
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
                text = palette.description,
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
                text = palette.name,
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