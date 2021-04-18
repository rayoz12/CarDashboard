package me.rytek.cardashboardserverv2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.rytek.cardashboardserverv2.ui.theme.CarDashboardServerV2Theme

class MainActivity : ComponentActivity() {
    private lateinit var mService: ServerListenerService
    private var mBound: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarDashboardServerV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityComposable()
                }
            }
        }
    }
}

@Composable
fun MainActivityComposable() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Car Dashboard Server") }
        )
        var state by remember { mutableStateOf("Not Connected") }
        Text(text = "State: $state")
        Box(modifier = Modifier.padding(vertical = 10.dp))
        Button(onClick = {
            GlobalScope.launch {
                state = "Connecting..."
                val success = connectionManager.mqttConnect()
                state = if (success) "Connected & Subscribed" else "Connection Error"
            }

        }) {
            Text("Connect")
        }
        Box(modifier = Modifier.padding(vertical = 10.dp))
//        Button(onClick = {
//            GlobalScope.launch {
//                val success = mqttInterface.subscribe("glass2k@ymail.com/CarDashboard")
//                state = if (success) "Subscribed" else "Subscription Error"
//            }
//        }) {
//            Text("Subscribe")
//        }
        Box(modifier = Modifier.padding(vertical = 10.dp))
        Button(onClick = {
            GlobalScope.launch {
                state = "Disconnecting..."
                val success = connectionManager.mqttDisconnect()
                state = if (success) "Not Connected" else "Failed to Disconnect!"
            }
        }) {
            Text("Disconnect")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CarDashboardServerV2Theme {
        MainActivityComposable()
    }
}