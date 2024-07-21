package com.example.dataextractionapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dataextractionapp.ui.theme.DataExtractionAppTheme
import com.example.sdk.DataExtractor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DataExtractionAppTheme {
                MainScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataExtractor = remember { DataExtractor(context) }
    var deviceData by remember { mutableStateOf<Map<String, String>?>(null) }
    var mediaMetadata by remember { mutableStateOf<List<Map<String, String>>?>(null) }
    var selectedScreen by remember { mutableStateOf(BottomNavScreen.DeviceData) }
    var contacts by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var contactsWithMultipleNumbers by remember { mutableStateOf(0) }
    var resolutionDistribution by remember { mutableStateOf<Map<String, Int>?>(null) }


    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO

            )
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        scope.launch {
            contacts = dataExtractor.getContactsAsync(context.contentResolver)
            contacts?.let {
                contactsWithMultipleNumbers =
                    dataExtractor.countContactsWithMultiplePhoneNumbers(it)
            }
        }

        deviceData = dataExtractor.getDeviceData()
        mediaMetadata = dataExtractor.getMediaMetadata()
        resolutionDistribution =
            dataExtractor.analyzeImageResolutionDistribution(mediaMetadata!!)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (permissionsState.allPermissionsGranted) {
                ComposableFactory.BottomNavigationBar(selectedScreen) { screen ->
                    selectedScreen = screen
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (permissionsState.allPermissionsGranted && deviceData != null && mediaMetadata != null) {
                when (selectedScreen) {
                    BottomNavScreen.DeviceData -> ComposableFactory.DeviceDataDisplay(deviceData!!)
                    BottomNavScreen.Images -> ComposableFactory.MediaMetadataDisplay("Images Metadata", mediaMetadata!!.filter { it["Duration"] == "N/A" })
                    BottomNavScreen.Videos -> ComposableFactory.MediaMetadataDisplay("Videos Metadata", mediaMetadata!!.filter { it["Duration"] != "N/A" })
                    BottomNavScreen.Analysis -> ComposableFactory.AnalysisDisplay(
                        contactsWithMultipleNumbers = contactsWithMultipleNumbers,
                        resolutionDistribution = resolutionDistribution
                    )
                }
            }
        }
    }
}

enum class BottomNavScreen {
    DeviceData, Images, Videos, Analysis
}

@Composable
fun DataDisplay(
    deviceData: Map<String, String>,
    mediaMetadata: List<Map<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        ComposableFactory.DeviceDataDisplay(deviceData)
        Spacer(modifier = Modifier.height(16.dp))
        ComposableFactory.MediaMetadataDisplay("Media Metadata", mediaMetadata)
    }
}

@Preview(showBackground = true)
@Composable
fun DataDisplayPreview() {
    val deviceData = remember {
        mapOf(
            "Device Model" to "Pixel 4",
            "OS Version" to "11",
            "Manufacturer" to "Google",
            "Screen Resolution" to "1080 x 1920"
        )
    }
    val mediaMetadata = remember {
        listOf(
            mapOf(
                "File Name" to "image1.jpg",
                "Date Created" to "2022-01-01",
                "File Size" to "2 MB",
                "Dimensions" to "1920 x 1080",
                "Duration" to "N/A"
            ),
            mapOf(
                "File Name" to "video1.mp4",
                "Date Created" to "2022-01-02",
                "File Size" to "20 MB",
                "Dimensions" to "1920 x 1080",
                "Duration" to "2 minutes"
            )
        )
    }

    DataExtractionAppTheme {
        DataDisplay(deviceData = deviceData, mediaMetadata = mediaMetadata)
    }
}
