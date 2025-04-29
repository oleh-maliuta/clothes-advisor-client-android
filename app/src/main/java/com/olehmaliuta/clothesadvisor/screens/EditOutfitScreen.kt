package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.components.ClothingItemCard
import com.olehmaliuta.clothesadvisor.components.OkDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.viewmodels.ClothingItemViewModel
import com.olehmaliuta.clothesadvisor.viewmodels.OutfitViewModel

@Composable
fun EditOutfitScreen(
    router: Router,
    clothingItemViewModel: ClothingItemViewModel,
    outfitViewModel: OutfitViewModel
) {
    var name by remember { mutableStateOf("") }
    var itemIds by remember { mutableStateOf(emptySet<Int>()) }

    val currentOutfit by outfitViewModel
        .getOutfitToEdit(outfitViewModel.idOfOutfitToEdit.value)
        .collectAsState(initial = null)
    val items by clothingItemViewModel
        .getItemsByIds(itemIds.toList())
        .collectAsState(initial = null)

    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isDeleteAcceptDialogOpened by remember { mutableStateOf(false) }

    val isNameValid by remember(name) {
        derivedStateOf { name.isNotBlank() }
    }

    LaunchedEffect(currentOutfit) {
        currentOutfit?.let { outfit ->
            name = outfit.name
            itemIds = outfit.itemIds.toSet()
        }
    }

    LaunchedEffect(outfitViewModel.outfitUploadingState) {
        when (val apiState = outfitViewModel.outfitUploadingState) {
            is ApiState.Success -> {
                outfitViewModel.idOfOutfitToEdit.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle = "Error"
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    OkDialog(
        title = okDialogTitle,
        content = okDialogMessage,
        onConfirm = {
            okDialogMessage = null
        }
    )

    if (currentOutfit != null) {
        AcceptCancelDialog(
            isOpened = isDeleteAcceptDialogOpened,
            title = "Delete the outfit",
            onDismissRequest = {
                isDeleteAcceptDialogOpened = false
            },
            onAccept = {},
            acceptText = "Accept",
        ) {
            Text(
                text = "Are you sure you want to delete the outfit?",
                textAlign = TextAlign.Justify
            )
        }
    }

    if (
        outfitViewModel.idOfOutfitToEdit.value != null &&
        currentOutfit == null
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Loading...",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 30.sp)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (currentOutfit == null)
                    "Add New Outfit" else "Edit Outfit",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Main info",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 100) {
                        name = it
                    }
                },
                label = { Text("Name*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !isNameValid,
                supportingText = {
                    if (!isNameValid) {
                        Text("Name is required")
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (currentOutfit == null) {
                            outfitViewModel.addOutfit(
                                name,
                                itemIds.toList()
                            )
                        }
                    },
                    enabled = isNameValid
                ) {
                    Text(
                        text = if (currentOutfit == null)
                            "Add Outfit" else "Save Changes"
                    )
                }

                if (currentOutfit != null) {
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        onClick = {
                            isDeleteAcceptDialogOpened = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    onClick = {
                        router.navigateBack()
                    }
                ) {
                    Text("Cancel")
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 3.dp,
                color = Color.Gray
            )

            Column {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                items?.forEach { item ->
                    ClothingItemCard(
                        item = item,
                        onClick = {
                            clothingItemViewModel.idOfItemToEdit.value = item.id
                            router.navigate(Screen.EditClothingItem.name)
                        }
                    )
                }
            }
        }
    }
}