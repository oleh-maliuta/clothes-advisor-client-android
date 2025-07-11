package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.ui.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.ui.components.ColorPicker
import com.olehmaliuta.clothesadvisor.ui.components.DatePicker
import com.olehmaliuta.clothesadvisor.ui.components.FloatingPointNumberInput
import com.olehmaliuta.clothesadvisor.ui.components.ImagePicker
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.ui.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.ClothingItemViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants
import com.olehmaliuta.clothesadvisor.utils.FileTool
import com.olehmaliuta.clothesadvisor.utils.navigation.Router
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditClothingItemScreen(
    router: Router,
    authViewModel: AuthViewModel,
    clothingItemViewModel: ClothingItemViewModel
) {
    val context = LocalContext.current

    val currentItem by clothingItemViewModel
        .getItemToEdit(clothingItemViewModel.idOfItemToEdit.value)
        .collectAsState(initial = null)

    var imageUri by remember { mutableStateOf<String?>(null) }
    var color by remember { mutableStateOf(Color.Black) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("tshirt") }
    var season by remember { mutableStateOf("spring") }
    var material by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var price by remember { mutableStateOf<Double?>(null) }
    var purchaseDate by remember { mutableStateOf<Date?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    var isSeasonDropMenuOpen by remember { mutableStateOf(false) }
    var isCategoryDropMenuOpen by remember { mutableStateOf(false) }
    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isDeleteAcceptDialogOpen by remember { mutableStateOf(false) }

    val isNameValid by remember(name) {
        derivedStateOf { name.isNotBlank() }
    }
    val isMaterialValid by remember(material) {
        derivedStateOf { material.isNotBlank() }
    }
    val isFormValid = isNameValid &&
            isMaterialValid &&
            !imageUri.isNullOrBlank() &&
            clothingItemViewModel.itemUploadingState !is ApiState.Loading &&
            clothingItemViewModel.itemDeletingState !is ApiState.Loading

    val errorMessageTitle = stringResource(R.string.edit_clothing_item__error_message_title)

    LaunchedEffect(currentItem) {
        currentItem?.let { item ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            imageUri = item.filename
            color = Color(item.red, item.green, item.blue)
            name = item.name
            category = item.category
            season = item.season
            material = item.material
            brand = item.brand ?: ""
            price = item.price
            purchaseDate = if (item.purchaseDate != null)
                dateFormat.parse(item.purchaseDate) else null
            isFavorite = item.isFavorite == true
        }
    }

    LaunchedEffect(clothingItemViewModel.itemUploadingState) {
        when (val apiState = clothingItemViewModel.itemUploadingState) {
            is ApiState.Success -> {
                clothingItemViewModel.idOfItemToEdit.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    LaunchedEffect(clothingItemViewModel.itemDeletingState) {
        when (val apiState = clothingItemViewModel.itemDeletingState) {
            is ApiState.Success -> {
                router.navigateBack()
            }
            is ApiState.Error -> {
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    LaunchedEffect(clothingItemViewModel.backgroundRemovingState) {
        when (val apiState = clothingItemViewModel.backgroundRemovingState) {
            is ApiState.Success -> {
                imageUri = apiState.data.toUri().toString()
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

    if (currentItem != null) {
        AcceptCancelDialog(
            isOpen = isDeleteAcceptDialogOpen,
            title = stringResource(
                R.string.edit_clothing_item__delete_item_message_title),
            onDismiss = {
                isDeleteAcceptDialogOpen = false
            },
            onAccept = {
                clothingItemViewModel
                    .deleteClothingItem(
                        context,
                        currentItem!!.id
                    )
            },
        ) {
            Text(
                text = stringResource(
                    R.string.edit_clothing_item__delete_item_message_description)
            )
        }
    }

    if (
        clothingItemViewModel.idOfItemToEdit.value != null &&
        currentItem == null
        ) {
        Spacer(modifier = Modifier.height(10.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("main_content_container"),
        ) {
            Text(
                text = if (currentItem == null)
                    stringResource(R.string.edit_clothing_item__new__title) else
                        stringResource(R.string.edit_clothing_item__edit__title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.edit_clothing_item__image__title),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            ImagePicker(
                currentImageUri = imageUri,
                onImageSelected = { uri ->
                    imageUri = uri?.toString() ?: ""
                    clothingItemViewModel.cancelBackgroundRemoving()
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (
                currentItem != null &&
                imageUri != currentItem!!.filename &&
                clothingItemViewModel.backgroundRemovingState
                        !is ApiState.Success
                ) {
                Button(
                    onClick = {
                        imageUri = currentItem!!.filename
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(
                            R.string.edit_clothing_item__image__return_the_original),
                    )
                }
            }

            if (
                !imageUri.isNullOrBlank() &&
                currentItem != null
                ) {
                if (
                    clothingItemViewModel.backgroundRemovingState
                            !is ApiState.Success
                    ) {
                    if (imageUri == currentItem!!.filename) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                clothingItemViewModel.getImageWithNoBackground(
                                    context,
                                    currentItem!!.id
                                )
                            },
                            enabled = authViewModel.authState.value
                                    is AuthState.Authenticated &&
                                    clothingItemViewModel.backgroundRemovingState
                                            !is ApiState.Loading,
                        ) {
                            if (
                                clothingItemViewModel.backgroundRemovingState
                                        is ApiState.Loading
                                ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(
                                        R.string.edit_clothing_item__image__remove_background),
                                )
                            }
                        }

                        if (authViewModel.authState.value is AuthState.Unauthenticated) {
                            Text(
                                text = stringResource(
                                    R.string.edit_clothing_item__image__auth_required),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            clothingItemViewModel.cancelBackgroundRemoving()
                            imageUri = currentItem!!.filename
                        },
                        enabled = authViewModel.authState.value
                                is AuthState.Authenticated &&
                                clothingItemViewModel.backgroundRemovingState
                                        !is ApiState.Loading,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.edit_clothing_item__image__remove_background_cancel),
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 22.dp),
                thickness = 3.dp,
                color = Color.Gray
            )

            Text(
                text = stringResource(R.string.edit_clothing_item__color__title),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ColorPicker(
                color = color,
                imageUri = imageUri,
                onColorChange = { selectedColor ->
                    color = selectedColor
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 22.dp),
                thickness = 3.dp,
                color = Color.Gray
            )

            Text(
                text = stringResource(R.string.edit_clothing_item__text__title),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 100) {
                        name = it
                    }
                },
                label = { Text(stringResource(R.string.edit_clothing_item__text__name__label)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input"),
                isError = !isNameValid,
                supportingText = {
                    if (!isNameValid) {
                        Text(stringResource(R.string.edit_clothing_item__text__name__not_valid))
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stringResource(AppConstants.categories.getValue(category)),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.edit_clothing_item__text__category__label)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        isCategoryDropMenuOpen = true
                                    }
                                }
                            }
                        }
                )
                DropdownMenu(
                    expanded = isCategoryDropMenuOpen,
                    onDismissRequest = { isCategoryDropMenuOpen = false }
                ) {
                    AppConstants.categories.forEach { categoryOption ->
                        DropdownMenuItem(
                            text = { Text(stringResource(categoryOption.value)) },
                            onClick = {
                                category = categoryOption.key
                                isCategoryDropMenuOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stringResource(AppConstants.seasons.getValue(season)),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.edit_clothing_item__text__season__label)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        isSeasonDropMenuOpen = true
                                    }
                                }
                            }
                        }
                )
                DropdownMenu(
                    expanded = isSeasonDropMenuOpen,
                    onDismissRequest = { isSeasonDropMenuOpen = false }
                ) {
                    AppConstants.seasons.forEach { seasonOption ->
                        DropdownMenuItem(
                            text = { Text(stringResource(seasonOption.value)) },
                            onClick = {
                                season = seasonOption.key
                                isSeasonDropMenuOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = material,
                onValueChange = {
                    if (it.length <= 50) {
                        material = it
                    }
                },
                label = { Text(stringResource(R.string.edit_clothing_item__text__material__label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !isMaterialValid,
                supportingText = {
                    if (!isMaterialValid) {
                        Text(stringResource(R.string.edit_clothing_item__text__material__not_valid))
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = {
                    if (it.length <= 100) {
                        brand = it
                    }
                },
                label = { Text(stringResource(R.string.edit_clothing_item__text__brand__label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FloatingPointNumberInput(
                value = price,
                onValueChange = { price = it },
                label = stringResource(R.string.edit_clothing_item__text__price__label),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePicker(
                selectedDate = purchaseDate,
                onDateSelected = { newDate -> purchaseDate = newDate },
                label = stringResource(R.string.edit_clothing_item__text__purchase_date__label),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .testTag("is_favorite_switch")
                        .semantics {
                        this.contentDescription =
                            if (isFavorite) "Checked" else "Unchecked"
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.edit_clothing_item__text__is_favorite__label),
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val dateFormatter = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault())
                        val newItem = ClothingItem(
                            id = currentItem?.id ?: 0,
                            filename = imageUri.toString(),
                            name = name,
                            category = category,
                            season = season,
                            red = (color.red * 255).toInt(),
                            green = (color.green * 255).toInt(),
                            blue = (color.blue * 255).toInt(),
                            material = material,
                            brand = brand.ifBlank { null },
                            purchaseDate = if (purchaseDate != null)
                                dateFormatter.format(purchaseDate ?: Date()) else null,
                            price = price,
                            isFavorite = isFavorite
                        )

                        if (currentItem == null) {
                            val file = FileTool.getFileFromUri(
                                context,
                                newItem.filename.toUri()) ?: return@Button

                            clothingItemViewModel.addClothingItem(
                                context,
                                file,
                                newItem
                            )
                        } else {
                            val file = if (imageUri != currentItem?.filename)
                                FileTool.getFileFromUri(
                                    context,
                                    newItem.filename.toUri()
                                ) else null

                            clothingItemViewModel.updateClothingItem(
                                context,
                                file,
                                newItem
                            )
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .testTag("apply_button")
                ) {
                    if (clothingItemViewModel.itemUploadingState is ApiState.Loading) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Text(
                            text = if (currentItem == null)
                                stringResource(R.string.edit_clothing_item__add_button) else
                                stringResource(R.string.edit_clothing_item__save_button)
                        )
                    }
                }

                if (currentItem != null) {
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        onClick = {
                            isDeleteAcceptDialogOpen = true
                        },
                        enabled = clothingItemViewModel
                            .itemUploadingState !is ApiState.Loading &&
                                clothingItemViewModel
                                    .itemDeletingState !is ApiState.Loading
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
                        clothingItemViewModel.idOfItemToEdit.value = null
                        router.navigateBack()
                    },
                    modifier = Modifier
                        .testTag("cancel_button")
                ) {
                    Text(stringResource(R.string.edit_clothing_item__cancel_button))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}