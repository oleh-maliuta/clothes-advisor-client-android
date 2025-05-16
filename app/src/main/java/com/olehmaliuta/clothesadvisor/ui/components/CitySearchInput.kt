package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.types.LocationInfo
import com.olehmaliuta.clothesadvisor.ui.viewmodels.RecommendationViewModel
import kotlinx.coroutines.delay

@Composable
fun CitySearchInput(
    query: String,
    value: LocationInfo?,
    viewModel: RecommendationViewModel,
    onQueryChange: (String) -> Unit,
    onLocationSelect: (LocationInfo?) -> Unit,
    isEnabled: Boolean = true
) {
    var suggestions by remember { mutableStateOf<List<LocationInfo>?>(null) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        if (query.length >= 3) {
            delay(1000)
            viewModel.searchLocations(query)
        }
    }

    LaunchedEffect(viewModel.locationSearchingState) {
        when (val apiState = viewModel.locationSearchingState) {
            is ApiState.Success -> {
                suggestions = apiState.data
            }
            is ApiState.Error -> {
                error = apiState.message.toString()
            }
            else -> {}
        }
    }

    if (isEnabled) {
        if (value == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = "City not selected",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 7.dp)
                )
            }
        } else {
            Card(
                onClick = { onLocationSelect(null) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 7.dp)
                ) {
                    Column {
                        Text(
                            text = "${value.nameLocal} (${value.nameEng})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Country: ${value.country}"
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove location"
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))
        }

        Spacer(modifier = Modifier.height(5.dp))
    }

    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        label = { Text("Search and select a city") },
        singleLine = true,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )

    if (isEnabled) {
        Spacer(modifier = Modifier.height(5.dp))

        Column {
            if (viewModel.locationSearchingState is ApiState.Loading) {
                CircularProgressIndicator()
            } else if (viewModel.locationSearchingState is ApiState.Error) {
                Text(
                    text = error,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                suggestions?.forEach { location ->
                    LocationInfoCard(
                        location = location,
                        onSelect = { onLocationSelect(location) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInfoCard(
    location: LocationInfo,
    onSelect: () -> Unit
) {
    Card(
        onClick = { onSelect() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 7.dp)
        ) {
            Text(
                text = "${location.nameLocal} (${location.nameEng})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Country: ${location.country}"
            )
        }
    }
}