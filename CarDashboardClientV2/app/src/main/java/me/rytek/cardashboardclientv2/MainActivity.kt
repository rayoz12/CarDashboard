package me.rytek.cardashboardclientv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import me.rytek.cardashboardclientv2.ui.theme.CarDashboardClientV2Theme

class MainActivity : ComponentActivity() {

    val mConnectionManager = ConnectionManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup connection manager
        mConnectionManager.setContext(this)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                Log.d("MainActivity", "Intent Received")
                Log.d("MainActivity", "Type: ${intent.type}, Value: ${intent.getStringExtra(Intent.EXTRA_TEXT).toString()}")
                // Check if this is spotify

            }
//            intent?.action == Intent.ACTION_SEND_MULTIPLE
//                    && intent.type?.startsWith("image/") == true -> {
//                handleSendMultipleImages(intent) // Handle multiple images being sent
//            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }


        setContent {
            CarDashboardClientV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityComposable(mConnectionManager)
                }
            }
        }
    }

    override fun onDestroy() {
        GlobalScope.launch {
            mConnectionManager.mqttDisconnect()
        }
        super.onDestroy()

    }
}

@Composable
fun MainActivityComposable(connectionManager: ConnectionManager) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Car Dashboard Client") }
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
    CarDashboardClientV2Theme {
        MainActivityComposable(ConnectionManager())
    }
}