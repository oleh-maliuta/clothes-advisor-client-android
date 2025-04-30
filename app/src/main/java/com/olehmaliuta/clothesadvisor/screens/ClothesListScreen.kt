package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.components.ClothingItemCard
import com.olehmaliuta.clothesadvisor.components.InfoDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.viewmodels.ClothingItemViewModel

@Composable
fun ClothesListScreen(
    router: Router,
    clothingItemViewModel: ClothingItemViewModel
) {
    var searchInputValue by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(Pair<String, String>("name", "name")) }
    var ascSort by remember { mutableStateOf(true) }
    var categoryFilter by remember { mutableStateOf(emptyList<String>()) }
    var seasonFilter by remember { mutableStateOf(emptyList<String>()) }

    val itemCount by clothingItemViewModel.countClothingItems
        .collectAsState(initial = null)
    val searchResults by clothingItemViewModel
        .searchItems(searchQuery, sortBy.second, ascSort, categoryFilter, seasonFilter)
        .collectAsState(initial = null)

    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isFilterDialogOpen by remember { mutableStateOf(false) }

    var selectedSeasons by remember {
        mutableStateOf(listOf(
            FilterOption("winter", "Winter"),
            FilterOption("spring", "Spring"),
            FilterOption("summer", "Summer"),
            FilterOption("autumn", "Autumn")
        ))
    }
    var selectedCategories by remember {
        mutableStateOf(listOf(
            FilterOption("tshirt", "t-shirt"),
            FilterOption("pants", "pants"),
            FilterOption("jacket", "jacket"),
            FilterOption("dress", "dress"),
            FilterOption("skirt", "skirt"),
            FilterOption("shorts", "shorts"),
            FilterOption("hoodie", "hoodie"),
            FilterOption("sweater", "sweater"),
            FilterOption("coat", "coat"),
            FilterOption("blouse", "blouse"),
            FilterOption("shoes", "shoes"),
            FilterOption("accessories", "accessories"),
            FilterOption("boots", "boots"),
            FilterOption("sneakers", "sneakers"),
            FilterOption("sandals", "sandals"),
            FilterOption("hat", "hat"),
            FilterOption("scarf", "scarf"),
            FilterOption("gloves", "gloves"),
            FilterOption("socks", "socks"),
            FilterOption("underwear", "underwear"),
            FilterOption("swimwear", "swimwear"),
            FilterOption("belt", "belt"),
            FilterOption("bag", "bag"),
            FilterOption("watch", "watch"),
            FilterOption("jeans", "jeans"),
            FilterOption("leggings", "leggings"),
            FilterOption("tank_top", "tank_top"),
            FilterOption("overalls", "overalls"),
            FilterOption("beanie", "beanie"),
        ))
    }

    LaunchedEffect(clothingItemViewModel.isFavoriteTogglingState) {
        when (val apiState = clothingItemViewModel.isFavoriteTogglingState) {
            is ApiState.Error -> {
                okDialogTitle = "Error"
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
        title = "Filters",
        onDismissRequest = { isFilterDialogOpen = false },
        onAccept = {
            seasonFilter = selectedSeasons
                .filter { it.isSelected }
                .map { it.value }
            categoryFilter = selectedCategories
                .filter { it.isSelected }
                .map { it.value }
            isFilterDialogOpen = false
        },
        acceptText = "Apply",
    ) {
        FilterMenu(
            categories = selectedCategories,
            seasons = selectedSeasons,
            onSeasonSelected = { idx ->
                selectedSeasons = selectedSeasons.toMutableList().apply {
                    this[idx] = FilterOption(
                        this[idx].value,
                        this[idx].displayName,
                        !this[idx].isSelected
                    )
                }
            },
            onCategorySelected = { idx ->
                selectedCategories = selectedCategories.toMutableList().apply {
                    this[idx] = FilterOption(
                        this[idx].value,
                        this[idx].displayName,
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
                placeholder = { Text("Search") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (itemCount != null) {
                            if (itemCount!! < 100) {
                                clothingItemViewModel.idOfItemToEdit.value = null
                                router.navigate(Screen.EditClothingItem.name)
                            } else {
                                okDialogTitle = "Error"
                                okDialogMessage = "Item limit reached. " +
                                        "Maximum 100 clothing items allowed per user."
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Row {
                    var sortExpanded by remember { mutableStateOf(false) }

                    Box {
                        Button(
                            contentPadding = PaddingValues(
                                start = 11.dp,
                                end = 3.dp),
                            onClick = { sortExpanded = true }
                        ) {
                            Text("Sort by: ${sortBy.first}")
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
                                text = { Text("name") },
                                onClick = {
                                    sortBy = Pair("name", "name")
                                    sortExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("purchase date") },
                                onClick = {
                                    sortBy = Pair("purchase date", "purchase_date")
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
                        clothingItemViewModel.updateIsFavoriteValue(item.id)
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
                InfoMessage(
                    "You have no clothing items at the moment."
                )
            }
        }

        if (itemCount != 0L && searchResults?.isEmpty() == true) {
            item {
                InfoMessage(
                    "No items were found according to the parameters."
                )
            }
        }
    }
}

private data class FilterOption(
    val value: String,
    var displayName: String,
    var isSelected: Boolean = false
)

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
            text = "Seasons",
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
                        text = option.displayName,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Categories",
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
                        text = option.displayName,
                        fontSize = 16.sp
                    )
                }
            }
        }

    }
}

@Composable
private fun InfoMessage(
    text: String
) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = text,
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