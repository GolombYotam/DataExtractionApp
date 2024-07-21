package com.example.dataextractionapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object ComposableFactory {

    @Composable
    fun BottomNavigationBar(selectedScreen: BottomNavScreen, onScreenSelected: (BottomNavScreen) -> Unit) {
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Phone, contentDescription = "Device Data") },
                label = { Text("Device Data") },
                selected = selectedScreen == BottomNavScreen.DeviceData,
                onClick = { onScreenSelected(BottomNavScreen.DeviceData) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Face, contentDescription = "Images") },
                label = { Text("Images") },
                selected = selectedScreen == BottomNavScreen.Images,
                onClick = { onScreenSelected(BottomNavScreen.Images) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Videos") },
                label = { Text("Videos") },
                selected = selectedScreen == BottomNavScreen.Videos,
                onClick = { onScreenSelected(BottomNavScreen.Videos) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Info, contentDescription = "Analysis") },
                label = { Text("Analysis") },
                selected = selectedScreen == BottomNavScreen.Analysis,
                onClick = { onScreenSelected(BottomNavScreen.Analysis) }
            )
        }
    }

    @Composable
    fun AnalysisDisplay(
        contactsWithMultipleNumbers: Int,
        resolutionDistribution: Map<String, Int>?,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            item {
                Text(text = "Analysis Results", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Contacts with multiple phone numbers: $contactsWithMultipleNumbers", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Image Resolution Distribution:", style = MaterialTheme.typography.bodyLarge)
            }
            resolutionDistribution?.forEach { (resolution, count) ->
                item {
                    Text(text = "$resolution: $count", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    @Composable
    fun MediaMetadataDisplay(title: String, mediaMetadata: List<Map<String, String>>, modifier: Modifier = Modifier) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(mediaMetadata) { metadata ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            metadata.forEach { (key, value) ->
                                if (!(key == "Duration" && value == "N/A")) {
                                    Text(text = "$key: $value", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DeviceDataDisplay(deviceData: Map<String, String>, modifier: Modifier = Modifier) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(text = "Device Data", style = MaterialTheme.typography.headlineMedium)
            deviceData.forEach { (key, value) ->
                Text(text = "$key: $value", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
