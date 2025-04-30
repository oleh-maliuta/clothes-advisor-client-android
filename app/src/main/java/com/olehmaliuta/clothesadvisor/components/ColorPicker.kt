package com.olehmaliuta.clothesadvisor.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.olehmaliuta.clothesadvisor.R

@Composable
fun ColorPicker(
    color: Color,
    imageUri: String?,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var red by remember(color) { mutableFloatStateOf(color.red) }
    var green by remember(color) { mutableFloatStateOf(color.green) }
    var blue by remember(color) { mutableFloatStateOf(color.blue) }

    var isPipetteMenuOpen by remember { mutableStateOf(false) }
    var pipetteColor by remember { mutableStateOf(Color.Black) }
    var imageRequest by remember { mutableStateOf<ImageRequest?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    val updateColor: () -> Unit = {
        onColorChange(Color(red, green, blue))
    }

    LaunchedEffect(isPipetteMenuOpen) {
        if (isPipetteMenuOpen) {
            touchPosition = Offset(
                imageSize.width.toFloat() / 2,
                imageSize.height.toFloat() / 2)
        }
    }

    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            val resultImageUrl = if (imageUri.startsWith("http"))
                imageUri.replace("://localhost", "://10.0.2.2") else
                imageUri
            val request = ImageRequest.Builder(context)
                .data(resultImageUrl)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .setHeader(
                    "Cache-Control",
                    "no-store, no-cache, must-revalidate")
                .setHeader("Pragma", "no-cache")
                .setHeader("Expires", "0")
                .allowHardware(false)
                .target { drawable ->
                    val bitmapDrawable = drawable as? BitmapDrawable
                    bitmap = bitmapDrawable?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                }
                .build()
            val imageLoader = ImageLoader.Builder(context).build()
            imageLoader.execute(request)
        }
    }

    AcceptCancelDialog(
        isOpen = isPipetteMenuOpen,
        title = "Pick a color",
        onDismissRequest = { isPipetteMenuOpen = false },
        onAccept = {
            red = pipetteColor.red
            green = pipetteColor.green
            blue = pipetteColor.blue
            updateColor()
            isPipetteMenuOpen = false
        },
        acceptText = "Apply"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (imageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                touchPosition = offset
                                bitmap?.let { bmp ->
                                    val x = (offset.x / size.width * bmp.width)
                                        .toInt().coerceIn(0, bmp.width - 1)
                                    val y = (offset.y / size.height * bmp.height)
                                        .toInt().coerceIn(0, bmp.height - 1)
                                    val pixel = bmp[x, y]
                                    pipetteColor = Color(
                                        red = android.graphics.Color
                                            .red(pixel) / 255f,
                                        green = android.graphics.Color
                                            .green(pixel) / 255f,
                                        blue = android.graphics.Color
                                            .blue(pixel) / 255f
                                    )
                                }
                            }
                        }
                ) {
                    val resultImageUrl = if (imageUri.startsWith("http"))
                        imageUri.replace("://localhost", "://10.0.2.2") else
                        imageUri
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(resultImageUrl)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .setHeader(
                                "Cache-Control",
                                "no-store, no-cache, must-revalidate")
                            .setHeader("Pragma", "no-cache")
                            .setHeader("Expires", "0")
                            .build()
                    )

                    Image(
                        painter = painter,
                        contentDescription = "Image to pick color from",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = pipetteColor,
                            center = touchPosition,
                            radius = 30f,
                            style = Stroke(width = 3f)
                        )
                        drawLine(
                            color = Color.Black,
                            start = Offset(touchPosition.x, 0f),
                            end = Offset(touchPosition.x, size.height),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, touchPosition.y),
                            end = Offset(size.width, touchPosition.y),
                            strokeWidth = 3f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(pipetteColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "RGB: (${(pipetteColor.red * 255).toInt()}, " +
                                "${(pipetteColor.green * 255).toInt()}, " +
                                "${(pipetteColor.blue * 255).toInt()})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap on the image to pick a color",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("No image available for color picking")
            }
        }
    }

    Column(modifier = modifier) {
        Row {
            if (imageUri != null) {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = { isPipetteMenuOpen  = true },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.pipette),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(red, green, blue))
                .border(1.dp, MaterialTheme.colorScheme.outline)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = red,
            onValueChange = { newValue ->
                red = newValue
                updateColor()
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Red
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        Slider(
            value = green,
            onValueChange = { newValue ->
                green = newValue
                updateColor()
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTrackColor = Color.Green
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        Slider(
            value = blue,
            onValueChange = { newValue ->
                blue = newValue
                updateColor()
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color.Blue,
                activeTrackColor = Color.Blue
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "RGB: (" +
                    "${(red * 255).toInt()}, " +
                    "${(green * 255).toInt()}, " +
                    "${(blue * 255).toInt()})",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}