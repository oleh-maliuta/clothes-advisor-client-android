package com.olehmaliuta.clothesadvisor.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.utils.FileTool

@Composable
fun ImagePicker(
    currentImageUri: String?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "image_picker"
) {
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val context = LocalContext.current

    var showRationale by remember { mutableStateOf(false) }
    var showPermanentDenial by remember { mutableStateOf(false) }
    var permissionRequestCount by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.any { it.value }
        if (!allGranted) {
            if (requiredPermissions.all { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        permission
                    )
                }) {
                showRationale = true
            } else {
                showPermanentDenial = true
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            FileTool.persistUriPermission(context, uri)
        }
        onImageSelected(uri)
    }

    AcceptCancelDialog(
        isOpen = showRationale,
        title = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                stringResource(R.string.permissions__read_media_images__title)
            else ->
                stringResource(R.string.permissions__read_external_storage__title)
        },
        onDismiss = { showRationale = false },
        onAccept = {
            showRationale = false
            permissionLauncher.launch(requiredPermissions)
        },
        acceptText = stringResource(R.string.permissions__try_again_button),
    ) {
        Text(stringResource(R.string.permissions__reason__select_images))
    }

    AcceptCancelDialog(
        isOpen = showPermanentDenial,
        title = stringResource(R.string.permissions__permanent__required),
        onDismiss = { showPermanentDenial = false },
        onAccept = {
            showPermanentDenial = false
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
        acceptText = stringResource(R.string.permissions__open_settings_button),
    ) {
        Text(
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    stringResource(R.string.permissions__read_media_images__permanent)
                else ->
                    stringResource(R.string.permissions__read_external_storage__permanent)
            }
        )
    }

    Column(
        modifier = modifier
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .testTag("${testTag}__box")
                .clickable {
                    if (requiredPermissions.any { permission ->
                            ContextCompat.checkSelfPermission(context, permission) ==
                                    PackageManager.PERMISSION_GRANTED
                        }) {
                        imagePickerLauncher.launch("image/*")
                    } else if (requiredPermissions.all { permission ->
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                permission
                            )
                        }) {
                        showRationale = true
                    } else {
                        if (permissionRequestCount < 2) {
                            permissionRequestCount++
                            permissionLauncher.launch(requiredPermissions)
                        } else {
                            showPermanentDenial = true
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (!currentImageUri.isNullOrBlank()) {
                val resultImageUrl = if (currentImageUri.startsWith("http"))
                    currentImageUri.replace("://localhost", "://10.0.2.2") else
                    currentImageUri

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(resultImageUrl)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                        .setHeader("Pragma", "no-cache")
                        .setHeader("Expires", "0")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Fit,
                    error = rememberVectorPainter(Icons.Default.Warning),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add image",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(stringResource(R.string.image_picker__select_image))
                }
            }
        }

        if (!currentImageUri.isNullOrBlank()) {
            Text(
                text = currentImageUri,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("${testTag}__path")
            )
        }
    }
}