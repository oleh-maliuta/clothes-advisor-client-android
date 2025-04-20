package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var red by remember(color) { mutableFloatStateOf(color.red) }
    var green by remember(color) { mutableFloatStateOf(color.green) }
    var blue by remember(color) { mutableFloatStateOf(color.blue) }

    val updateColor: () -> Unit = {
        onColorChange(Color(red, green, blue))
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(red, green, blue))
                .border(1.dp, MaterialTheme.colorScheme.outline)
        )

        ColorSlider(
            value = red,
            onValueChange = { newValue ->
                red = newValue
                updateColor()
            },
            color = Color.Red,
            label = "Red"
        )

        ColorSlider(
            value = green,
            onValueChange = { newValue ->
                green = newValue
                updateColor()
            },
            color = Color.Green,
            label = "Green"
        )

        ColorSlider(
            value = blue,
            onValueChange = { newValue ->
                blue = newValue
                updateColor()
            },
            color = Color.Blue,
            label = "Blue"
        )
    }
}

@Composable
private fun ColorSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "$label: ${(value * 255).toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
    }
}