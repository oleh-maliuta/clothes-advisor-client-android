package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.viewmodels.ClothingItemViewModel
import java.io.File

@Composable
fun ClothesListScreen(
    clothingItemViewModel: ClothingItemViewModel
) {
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

    var isFilterDialogOpened by remember { mutableStateOf(false) }

    var selectedCategories by remember {
        mutableStateOf(mapOf(
            "pants" to FilterOption("pants", false),
            "jacket" to FilterOption("jacket", false),
            "tshirt" to FilterOption("t-shirt", false),
            "dress" to FilterOption("dress", false)
        ))
    }
    var selectedSeasons by remember {
        mutableStateOf(mapOf(
            "winter" to FilterOption("Winter", false),
            "spring" to FilterOption("Spring", false),
            "summer" to FilterOption("Summer", false),
            "autumn" to FilterOption("Autumn", false)
        ))
    }

    AcceptCancelDialog(
        isOpened = isFilterDialogOpened,
        title = "Filters",
        onDismissRequest = { isFilterDialogOpened = false },
        onAccept = {
            categoryFilter = selectedCategories
                .filterValues { it.isSelected }
                .keys.toList()
            seasonFilter = selectedSeasons
                .filterValues { it.isSelected }
                .keys.toList()
            isFilterDialogOpened = false
        },
        acceptText = "Apply",
    ) {
        FilterMenu(
            categories = selectedCategories,
            seasons = selectedSeasons,
            onCategorySelected = { key ->
                selectedCategories = selectedCategories.toMutableMap().apply {
                    this[key] = this[key]?.copy(isSelected = !this[key]?.isSelected!!)
                            as FilterOption
                }
            },
            onSeasonSelected = { key ->
                selectedSeasons = selectedSeasons.toMutableMap().apply {
                    this[key] = this[key]?.copy(isSelected = !this[key]?.isSelected!!)
                            as FilterOption
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
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp)),
                placeholder = { Text("Search") },
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
                    onClick = { /* Handle click */ }
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
                    onClick = { isFilterDialogOpened = true },
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
            itemCount != 0 &&
            !searchResults.isNullOrEmpty()
            ) {
            items(
                searchResults ?: emptyList()
            ) { item ->
                ClothingItemCard(
                    item = item,
                    onFavoriteClick = {},
                    onClick = {}
                )
            }
        } else if (itemCount == 0) {
            item {
                InfoMessage(
                    "You have no clothing items at the moment."
                )
            }
        } else {
            item {
                InfoMessage(
                    "Loading..."
                )
            }
        }
    }
}

private data class FilterOption(val displayName: String, var isSelected: Boolean)

@Composable
private fun ClothingItemCard(
    item: ClothingItem,
    modifier: Modifier = Modifier,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (item.filename.isNotEmpty()) {
                    when {
                        item.filename.startsWith("http") -> {
                            val resultImageUrl = item.filename.replace(
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
                        }
                        item.filename.startsWith("content://") ||
                                item.filename.startsWith("file://") -> {
                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(item.filename)
                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build()
                            )
                            Image(
                                painter = painter,
                                contentDescription = item.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        File(item.filename).exists() -> {
                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(File(item.filename).apply {
                                        setLastModified(System.currentTimeMillis())
                                    })
                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build()
                            )
                            Image(
                                painter = painter,
                                contentDescription = item.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Image(
                                painter = rememberVectorPainter(Icons.Default.Warning),
                                contentDescription = "Placeholder",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    Image(
                        painter = rememberVectorPainter(Icons.Default.Warning),
                        contentDescription = "Placeholder",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    if (onFavoriteClick != null) {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (item.isFavorite)
                                    Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val boxSize = with(LocalDensity.current) { 14.sp.toDp() }
                    val boxPadding = with(LocalDensity.current) {
                        4.7f.sp.toDp() * 0.9f
                    }

                    Text(
                        text = item.category,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp
                    )
                    Text(
                        text = item.season,
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
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Material: ${item.material}",
                    fontSize = 14.sp
                )

                item.brand?.let { brand ->
                    Text(
                        text = "Brand: $brand",
                        fontSize = 14.sp
                    )
                }

                item.price?.let { price ->
                    Text(
                        text = "Price: ${"%.2f".format(price)}",
                        fontSize = 14.sp
                    )
                }

                item.purchaseDate?.let { purchaseDate ->
                    Text(
                        text = "Purchase date: $purchaseDate",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterMenu(
    categories: Map<String, FilterOption>,
    seasons: Map<String, FilterOption>,
    onCategorySelected: (String) -> Unit,
    onSeasonSelected: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Column {
            categories.forEach { (key, category) ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(key) }
                ) {
                    Checkbox(
                        checked = category.isSelected,
                        onCheckedChange = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = category.displayName,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seasons",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Column {
            seasons.forEach { (key, season) ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeasonSelected(key) }
                ) {
                    Checkbox(
                        checked = season.isSelected,
                        onCheckedChange = null // Handled by parent click
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = season.displayName,
                        fontSize = 18.sp
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