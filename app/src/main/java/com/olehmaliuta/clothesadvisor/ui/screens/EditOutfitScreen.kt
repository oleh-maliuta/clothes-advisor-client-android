package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.ui.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.ui.components.ClothingItemCard
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.utils.navigation.Router
import com.olehmaliuta.clothesadvisor.ui.viewmodels.ClothingItemViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.OutfitViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.StorageViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants

@Composable
fun EditOutfitScreen(
    router: Router,
    clothingItemViewModel: ClothingItemViewModel,
    outfitViewModel: OutfitViewModel,
    storageViewModel: StorageViewModel
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var itemIds by remember { mutableStateOf(storageViewModel.initialItemIds.value) }

    val currentOutfit by outfitViewModel
        .getOutfitToEdit(outfitViewModel.idOfOutfitToEdit.value)
        .collectAsState(initial = null)
    val items by clothingItemViewModel
        .getItemsByIds(itemIds.toList())
        .collectAsState(initial = null)

    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isItemListDialogOpen by remember { mutableStateOf(false) }
    var isDeleteAcceptDialogOpen by remember { mutableStateOf(false) }

    val isNameValid by remember(name) {
        derivedStateOf { name.isNotBlank() }
    }
    val isFormValid = isNameValid &&
            outfitViewModel.outfitUploadingState !is ApiState.Loading &&
            outfitViewModel.outfitDeletingState !is ApiState.Loading

    Box(
        modifier = Modifier
            .testTag(
                "mode__" +
                        if (outfitViewModel.idOfOutfitToEdit.value == null)
                            "add" else "edit"
            )
    )

    LaunchedEffect(currentOutfit) {
        currentOutfit?.let { outfit ->
            name = outfit.outfit.name
            itemIds = outfit.clothingItems.map { item ->
                item.id
            }.toSet()
        }
    }

    val errorMessageTitle = stringResource(R.string.edit_outfit__error_message_title)

    LaunchedEffect(outfitViewModel.outfitUploadingState) {
        when (val apiState = outfitViewModel.outfitUploadingState) {
            is ApiState.Success -> {
                outfitViewModel.idOfOutfitToEdit.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    LaunchedEffect(outfitViewModel.outfitDeletingState) {
        when (val apiState = outfitViewModel.outfitDeletingState) {
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

    InfoDialog(
        title = okDialogTitle,
        content = okDialogMessage,
        onConfirm = {
            okDialogMessage = null
        }
    )

    SelectItemsForOutfitDialog(
        clothingItemViewModel = clothingItemViewModel,
        isOpen = isItemListDialogOpen,
        selectedItems = itemIds,
        onItemToggle = { itemId ->
            itemIds = if (itemIds.contains(itemId)) {
                itemIds - itemId
            } else {
                itemIds + itemId
            }
        },
        onConfirm = {
            isItemListDialogOpen = false
        }
    )

    if (currentOutfit != null) {
        AcceptCancelDialog(
            isOpen = isDeleteAcceptDialogOpen,
            title = stringResource(R.string.edit_outfit__delete_outfit_message_title),
            onDismiss = {
                isDeleteAcceptDialogOpen = false
            },
            onAccept = {
                outfitViewModel
                    .deleteOutfit(
                        context,
                        currentOutfit!!.outfit.id
                    )
            },
        ) {
            Text(
                text = stringResource(R.string.edit_outfit__delete_outfit_message_description)
            )
        }
    }

    if (
        outfitViewModel.idOfOutfitToEdit.value != null &&
        currentOutfit == null
    ) {
        Spacer(modifier = Modifier.height(10.dp))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("main_content_container"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (currentOutfit == null)
                    stringResource(R.string.edit_outfit__new__title) else
                    stringResource(R.string.edit_outfit__edit__title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.edit_outfit__main__title),
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
                label = { Text(stringResource(R.string.edit_outfit__main__name__label)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input"),
                isError = !isNameValid,
                supportingText = {
                    if (!isNameValid) {
                        Text(stringResource(R.string.edit_outfit__main__name__not_valid))
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
                                context,
                                name,
                                itemIds.toList()
                            )
                        } else {
                            outfitViewModel.updateOutfit(
                                context,
                                currentOutfit!!.outfit.id,
                                name,
                                itemIds.toList()
                            )
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .testTag("apply_button")
                ) {
                    Text(
                        text = if (currentOutfit == null)
                            stringResource(R.string.edit_outfit__add_button) else
                            stringResource(R.string.edit_outfit__save_button)
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
                            isDeleteAcceptDialogOpen = true
                        },
                        enabled = outfitViewModel
                            .outfitUploadingState !is ApiState.Loading &&
                                outfitViewModel
                                    .outfitDeletingState !is ApiState.Loading,
                        modifier = Modifier
                            .testTag("delete_button")
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
                        outfitViewModel.idOfOutfitToEdit.value = null
                        router.navigateBack()
                    },
                    modifier = Modifier
                        .testTag("cancel_button")
                ) {
                    Text(stringResource(R.string.edit_outfit__cancel_button))
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
                        .fillMaxWidth()
                        .testTag("edit_item_list_button"),
                    onClick = {
                        isItemListDialogOpen = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Edit the list",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                items?.forEach { item ->
                    ClothingItemCard(
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectItemsForOutfitDialog(
    clothingItemViewModel: ClothingItemViewModel,
    isOpen: Boolean,
    selectedItems: Set<Long>,
    onItemToggle: (Long) -> Unit,
    onConfirm: () -> Unit
) {
    if (!isOpen) {
        return
    }

    AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text(stringResource(R.string.edit_outfit__select_items__title)) },
        text = {
            var searchInputValue by remember { mutableStateOf("") }
            var searchQuery by remember { mutableStateOf("") }

            val items by clothingItemViewModel
                .searchItems(searchQuery, "name", true, emptyList(), emptyList())
                .collectAsState(initial = null)
            val selectedCategories by clothingItemViewModel
                .getUniqueCategoriesByIds(selectedItems.toList())
                .collectAsState(initial = null)

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                BasicTextField(
                    value = searchInputValue,
                    onValueChange = { searchInputValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchQuery = searchInputValue
                        }
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (items == null || selectedCategories == null) {
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
                } else if (searchQuery.isNotBlank() && items.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.edit_outfit__select_items__not_found),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                } else {
                    items?.forEach { item ->
                        ClothingItemMiniCard(
                            item = item,
                            isChecked = selectedItems.contains(item.id),
                            isBlocked = selectedCategories!!.contains(item.category) &&
                                    !selectedItems.contains(item.id),
                            onClick = { onItemToggle(item.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = onConfirm,
                modifier = Modifier
                    .testTag("select_items_for_outfit_dialog__done_button")
            ) {
                Text(stringResource(R.string.edit_outfit__select_items__done_button))
            }
        },
        modifier = Modifier
            .testTag("select_items_for_outfit_dialog")
    )
}

@Composable
private fun ClothingItemMiniCard(
    item: ClothingItem,
    isChecked: Boolean,
    isBlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = if (isBlocked) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ) else CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        onClick = {
            if (!isBlocked) {
                onClick?.invoke()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("clothing_item_mini_card"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                modifier = Modifier
                    .testTag("clothing_item_mini_card__checkbox")
                    .semantics {
                        this.contentDescription =
                            if (isChecked) "Checked" else "Unchecked"
                    }
            )

            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val boxSize = with(LocalDensity.current) {
                        14.sp.toDp()
                    }
                    val boxPadding = with(LocalDensity.current) {
                        4.7f.sp.toDp() * 0.9f
                    }

                    Text(
                        text = stringResource(
                            AppConstants.categories.getValue(item.category)),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(
                            AppConstants.seasons.getValue(item.season)),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp
                    )
                    Column {
                        Spacer(modifier = Modifier.height(boxPadding))

                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .clip(CircleShape)
                                .background(
                                    Color(
                                        red = item.red,
                                        green = item.green,
                                        blue = item.blue))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme
                                        .colorScheme.outline,
                                    shape = CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = stringResource(
                        R.string.edit_outfit__mini_card__material,
                        item.material),
                    fontSize = 14.sp
                )

                if (item.brand != null) {
                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = stringResource(
                            R.string.edit_outfit__mini_card__brand,
                            item.brand),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}