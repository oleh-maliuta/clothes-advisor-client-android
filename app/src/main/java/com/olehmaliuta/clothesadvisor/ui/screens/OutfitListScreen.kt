package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItemsCount
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.ui.viewmodels.OutfitViewModel

@Composable
fun OutfitListScreen(
    router: Router,
    outfitViewModel: OutfitViewModel
) {
    var searchInputValue by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }

    val outfitCount by outfitViewModel.countOutfits
        .collectAsState(initial = null)
    val searchResults by outfitViewModel
        .searchOutfits(searchQuery)
        .collectAsState(initial = null)

    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }

    InfoDialog(
        title = okDialogTitle,
        content = okDialogMessage,
        onConfirm = {
            okDialogMessage = null
        }
    )

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
                        shape = RoundedCornerShape(28.dp)
                    )
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
        }

        if (
            outfitCount != null &&
            outfitCount != 0L &&
            !searchResults.isNullOrEmpty()
        ) {
            items(
                searchResults ?: emptyList()
            ) { outfit ->
                OutfitCard(
                    outfit = outfit,
                    onClick = {
                        outfitViewModel.idOfOutfitToEdit.value = outfit.id
                        router.navigate(Screen.EditOutfit.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        if (outfitCount == 0L) {
            item {
                InfoMessage(
                    "You have no outfits at the moment."
                )
            }
        }

        if (outfitCount != 0L && searchResults?.isEmpty() == true) {
            item {
                InfoMessage(
                    "No outfits were found according to the parameters."
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                if (outfitCount != null) {
                    if (outfitCount!! < 50) {
                        outfitViewModel.idOfOutfitToEdit.value = null
                        router.navigate(Screen.EditOutfit.name)
                    } else {
                        okDialogTitle = "Error"
                        okDialogMessage = "Item limit reached. " +
                                "Maximum 50 outfits allowed per user."
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_outfit_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}

@Composable
fun OutfitCard(
    outfit: OutfitWithClothingItemsCount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = outfit.name,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Item count: ${outfit.itemCount}",
                color = if (outfit.itemCount == 0L)
                    MaterialTheme.colorScheme.error else Color.Unspecified,
                style = MaterialTheme.typography.bodyLarge
            )
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