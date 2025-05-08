package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.ui.components.PieChart
import com.olehmaliuta.clothesadvisor.ui.viewmodels.StatisticsViewModel
import com.olehmaliuta.clothesadvisor.utils.roundToDecimals
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel
) {
    val totalItemsCount by statisticsViewModel.totalItemsCount
        .collectAsState(initial = null)
    val itemsCountPerSeason by statisticsViewModel.itemsCountPerSeason
        .collectAsState(initial = null)
    val itemsCountPerCategory by statisticsViewModel.itemsCountPerCategory
        .collectAsState(initial = null)
    val oldestItems by statisticsViewModel.oldestItems
        .collectAsState(initial = null)
    val mostUsedItems by statisticsViewModel.mostUsedItems
        .collectAsState(initial = null)
    val favoriteItemsPercentage by statisticsViewModel.favoriteItemsPercentage
        .collectAsState(initial = null)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            if (totalItemsCount != null) {
                StatisticsCard(title = "Overall Statistics") {
                    KeyValueRow("Total Items", totalItemsCount.toString())

                    if (totalItemsCount != 0L) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Items by Season:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        itemsCountPerSeason?.forEach { (season, count) ->
                            KeyValueRow(season, count.toString())
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Items by Category:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        itemsCountPerCategory?.forEach { (category, count) ->
                            KeyValueRow(category, count.toString())
                        }
                    }
                }
            }
        }

        item {
            if (oldestItems != null) {
                StatisticsCard(title = "Age Statistics") {
                    if (!oldestItems!!.isEmpty()) {
                        Text(
                            text = "Oldest Items (top 5):",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        oldestItems?.forEach { item ->
                            KeyValueRow(item.name, item.purchaseDate ?: "Unknown")
                        }
                    } else {
                        InfoMessage("No items")
                    }
                }
            }
        }

        item {
            if (mostUsedItems != null) {
                StatisticsCard(title = "Usage in Outfits") {
                    if (!mostUsedItems!!.isEmpty()) {
                        Text(
                            text = "Most Used Items (top 10):",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        mostUsedItems?.forEach { item ->
                            KeyValueRow(item.name, "${item.usageCount} outfits")
                        }
                    } else {
                        InfoMessage("No items")
                    }
                }
            }
        }

        item {
            if (favoriteItemsPercentage != null) {
                StatisticsCard(title = "Favorite Items") {
                    if (
                        favoriteItemsPercentage!!.favoritePercentage != 0f &&
                        favoriteItemsPercentage!!.nonFavoritePercentage != 0f
                        ) {
                        PieChart(
                            data = mapOf(
                                "Favorite" to favoriteItemsPercentage!!
                                    .favoritePercentage,
                                "Regular" to favoriteItemsPercentage!!
                                    .nonFavoritePercentage
                            ),
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        KeyValueRow(
                            "Favorite Items",
                            "${favoriteItemsPercentage!!
                                .favoritePercentage.roundToDecimals(3)}%"
                        )
                        KeyValueRow(
                            "Regular Items",
                            "${favoriteItemsPercentage!!
                                .nonFavoritePercentage.roundToDecimals(3)}%"
                        )
                    } else {
                        InfoMessage("No items")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun KeyValueRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun InfoMessage(
    text: String
) {
    Text(
        text = text,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.error
    )
}