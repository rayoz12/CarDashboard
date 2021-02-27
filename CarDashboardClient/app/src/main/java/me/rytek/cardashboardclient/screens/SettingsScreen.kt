package me.rytek.cardashboardclient.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Preview
@Composable
fun SettingsPreview() {
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }

            },
            title = {
                Column {
                    Text(text = "Client")
                    Text(text = "Not Connected", style = MaterialTheme.typography.subtitle1)
                }
            }, actions = {
                IconButton(onClick = { Log.d("MainScreen", "Bluetooth Connect")}) {
                    Icon(imageVector = Icons.Filled.BluetoothConnected, contentDescription = "BT Connect")
                }
                IconButton(onClick = { Log.d("MainScreen", "WiFi Connect")}) {
                    Icon(imageVector = Icons.Filled.Wifi, contentDescription = "WiFi Connect")
                }
                IconButton(onClick = { Log.d("MainScreen", "MQTT Connect")}) {
                    Icon(imageVector = Icons.Filled.Cloud, contentDescription = "Cloud Connect")
                }
                IconButton(onClick = { Log.d("MainScreen", "Settings")}) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                }
            })
        }
    ) {
        SettingsScreen()
    }
}


@Composable
fun SettingsScreen() {
    Text("Hello Settings")
}