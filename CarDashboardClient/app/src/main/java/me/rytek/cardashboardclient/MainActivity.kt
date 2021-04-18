package me.rytek.cardashboardclient

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import me.rytek.cardashboardclient.interfaces.wifi.WifiInterface
import me.rytek.cardashboardclient.screens.DebugScreen
import me.rytek.cardashboardclient.screens.SettingsScreen
import me.rytek.cardashboardclient.services.CommunicationService
import me.rytek.cardashboardclient.viewmodel.BluetoothViewModel
import me.rytek.cardashboardclient.viewmodel.CommunicationViewModel
import me.rytek.cardashboardclient.viewmodel.SettingsViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class MainActivity : AppCompatActivity() {
    private val TAG = "Car Dashboard Server"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}

@Preview
@Composable
fun MainScreen() {

    val settingsViewModel: SettingsViewModel = viewModel()
    settingsViewModel.initFromDataStore(LocalContext.current.dataStore)

    val communicationViewModel: CommunicationViewModel = viewModel()
    communicationViewModel.init(settingsViewModel, LocalContext.current)

    val commsService: CommunicationService = communicationViewModel.commsService!!

    // Set up defaults
    settingsViewModel.setBrokerAddress("tcp://maqiatto.com:1883")
    settingsViewModel.setTopic("glass2k@ymail.com/CarDashboard")

    settingsViewModel.setServerAddress("192.168.1.101")
    settingsViewModel.setServerPort(1337)

    // val status: String? by viewModel.status.observeAsState("")
    val status = "NOT_USED"

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

    Scaffold(
            topBar = {
                TopAppBar(navigationIcon = {
                    if (currentRoute != "debug") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                title = {
                    Column {
                        Text(text = "Client")
                        if (status != "") {
                            Text(text = status, style = MaterialTheme.typography.subtitle1)
                        }
                    }
                }, actions = {
                    var wifiConnected by remember { mutableStateOf(false)}
                    var MQTTConnected by remember { mutableStateOf(false)}
                    IconButton(onClick = {Log.d("MainScreen", "Bluetooth Connect")}) {
                        Icon(imageVector = Icons.Filled.BluetoothConnected, contentDescription = "BT Connect")
                    }
                    IconButton(onClick = {
                        Log.d("MainScreen", "WiFi Connect")
                        commsService.wifiToggle()
                        wifiConnected = commsService.isWifiConnected()
                    }) {
                        if (wifiConnected) {
                            Icon(imageVector = Icons.Outlined.Wifi, contentDescription = "WiFi Connect")
                        }
                        else {
                            Icon(imageVector = Icons.Outlined.WifiOff, contentDescription = "WiFi Connect")
                        }

                    }
                    IconButton(onClick = {
                        Log.d("MainScreen", "MQTT Toggle")
                        commsService.mqttToggle()
                        MQTTConnected = commsService.isMQTTConnected()
                    }) {
                        if (MQTTConnected) {
                            Icon(imageVector = Icons.Filled.Cloud, contentDescription = "Cloud Connect")
                        }
                        else {
                            Icon(imageVector = Icons.Filled.CloudOff, contentDescription = "Cloud Connect")
                        }
                    }
                    IconButton(onClick = {Log.d("MainScreen", "Settings")}) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                    }
                })
            }
    ) {
        NavHost(navController, startDestination = "debug") {
            composable("debug") { DebugScreen(communicationViewModel) }
            composable("settings") { SettingsScreen() }
        }
    }
}
