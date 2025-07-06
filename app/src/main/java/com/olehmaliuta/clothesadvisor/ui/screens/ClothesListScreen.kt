package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.utils.types.FilterOption
import com.olehmaliuta.clothesadvisor.ui.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.ui.components.ClothingItemCard
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.utils.navigation.Router
import com.olehmaliuta.clothesadvisor.utils.navigation.Screen
import com.olehmaliuta.clothesadvisor.ui.viewmodels.ClothingItemViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants

@Composable
fun ClothesListScreen(
    router: Router,
    clothingItemViewModel: ClothingItemViewModel
) {
    val context = LocalContext.current

    var searchInputValue by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(Pair(
        "name", R.string.clothes_list__sort_option__name
    )) }
    var ascSort by remember { mutableStateOf(true) }
    var categoryFilter by remember { mutableStateOf(emptyList<String>()) }
    var seasonFilter by remember { mutableStateOf(emptyList<String>()) }

    val itemCount by clothingItemViewModel.countClothingItems
        .collectAsState(initial = null)
    val searchResults by clothingItemViewModel
        .searchItems(searchQuery, sortBy.first, ascSort, categoryFilter, seasonFilter)
        .collectAsState(initial = null)

    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isFilterDialogOpen by remember { mutableStateOf(false) }

    val errorMessageTitle = stringResource(R.string.clothes_list__error_message_title)

    var selectedSeasons by remember {
        mutableStateOf(AppConstants.seasons.map { seasonEntry ->
            FilterOption(seasonEntry.key, seasonEntry.value)
        })
    }
    var selectedCategories by remember {
        mutableStateOf(AppConstants.categories.map { categoryEntry ->
            FilterOption(categoryEntry.key, categoryEntry.value)
        })
    }

    LaunchedEffect(clothingItemViewModel.isFavoriteTogglingState) {
        when (val apiState = clothingItemViewModel.isFavoriteTogglingState) {
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
        isOpen = isFilterDialogOpen,
        title = stringResource(R.string.clothes_list__filters__title),
        onDismiss = { isFilterDialogOpen = false },
        onAccept = {
            seasonFilter = selectedSeasons
                .filter { it.isSelected }
                .map { it.value }
            categoryFilter = selectedCategories
                .filter { it.isSelected }
                .map { it.value }
            isFilterDialogOpen = false
        }
    ) {
        FilterMenu(
            categories = selectedCategories,
            seasons = selectedSeasons,
            onSeasonSelected = { idx ->
                selectedSeasons = selectedSeasons.toMutableList().apply {
                    this[idx] = FilterOption(
                        this[idx].value,
                        this[idx].displayNameId,
                        !this[idx].isSelected
                    )
                }
            },
            onCategorySelected = { idx ->
                selectedCategories = selectedCategories.toMutableList().apply {
                    this[idx] = FilterOption(
                        this[idx].value,
                        this[idx].displayNameId,
                        !this[idx].isSelected
                    )
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 8.dp)
            .testTag("main_content_container")
    ) {
        item {
            TextField(
                value = searchInputValue,
                onValueChange = { searchInputValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp)),
                placeholder = { Text(stringResource(R.string.clothes_list__search)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchQuery = searchInputValue
                    }
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    var sortExpanded by remember { mutableStateOf(false) }

                    Box {
                        Button(
                            contentPadding = PaddingValues(
                                start = 11.dp,
                                end = 3.dp),
                            onClick = { sortExpanded = true }
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.clothes_list__sort_by,
                                    stringResource(sortBy.second)
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Sort options"
                            )
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.clothes_list__sort_option__name)
                                    )
                                },
                                onClick = {
                                    sortBy = Pair(
                                        "name",
                                        R.string.clothes_list__sort_option__name
                                    )
                                    sortExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.clothes_list__sort_option__purchase_date)
                                    )
                                },
                                onClick = {
                                    sortBy = Pair(
                                        "purchase_date",
                                        R.string.clothes_list__sort_option__purchase_date
                                    )
                                    sortExpanded = false
                                }
                            )
                        }
                    }

                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { ascSort = !ascSort }
                    ) {
                        Icon(
                            imageVector = if (ascSort)
                                Icons.Filled.KeyboardArrowUp else
                                Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Sort Direction",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = { isFilterDialogOpen = true },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        if (
            itemCount != null &&
            itemCount != 0L &&
            !searchResults.isNullOrEmpty()
            ) {
            items(
                searchResults ?: emptyList()
            ) { item ->
                ClothingItemCard(
                    item = item,
                    onFavoriteClick = {
                        clothingItemViewModel.updateIsFavoriteValue(
                            context,
                            item.id
                        )
                    },
                    onClick = {
                        clothingItemViewModel.idOfItemToEdit.value = item.id
                        router.navigate(Screen.EditClothingItem.name)
                    }
                )
            }
        }

        if (itemCount == 0L) {
            item {
                Spacer(modifier = Modifier.height(30.dp))

                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(12.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon),
                                contentDescription = "App Icon",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .size(35.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.clothes_list__info__purpose),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            lineHeight = 30.sp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = stringResource(R.string.clothes_list__info__no_items),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            lineHeight = 30.sp)
                    )
                }
            }
        }

        if (itemCount != 0L && searchResults?.isEmpty() == true) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.clothes_list__info__no_items_found),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        lineHeight = 30.sp)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val itemLimitMessage = stringResource(
            R.string.clothes_list__item_limit_reached_message,
            AppConstants.MAX_CLOTHING_ITEMS)

        FloatingActionButton(
            onClick = {
                if (itemCount != null) {
                    if (itemCount!! < AppConstants.MAX_CLOTHING_ITEMS) {
                        clothingItemViewModel.idOfItemToEdit.value = null
                        router.navigate(Screen.EditClothingItem.name)
                    } else {
                        okDialogTitle = errorMessageTitle
                        okDialogMessage = itemLimitMessage
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_item_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun FilterMenu(
    seasons: List<FilterOption>,
    categories: List<FilterOption>,
    onSeasonSelected: (Int) -> Unit,
    onCategorySelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.clothes_list__filters__seasons__title),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            seasons.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeasonSelected(index) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = option.isSelected,
                        onCheckedChange = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(option.displayNameId),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.clothes_list__filters__categories__title),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            categories.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(index) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = option.isSelected,
                        onCheckedChange = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(option.displayNameId),
                        fontSize = 16.sp
                    )
                }
            }
        }

    }
}