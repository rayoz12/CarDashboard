package me.rytek.cardashboardclientv2

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import me.rytek.cardashboardclientv2.CarProtocol.ActionInterface
import me.rytek.cardashboardclientv2.CarProtocol.SpotifyPlayAction
import me.rytek.cardashboardclientv2.ui.theme.CarDashboardClientV2Theme
import me.rytek.cardashboardclientv2.CarProtocol.MessageType
import androidx.compose.ui.res.painterResource
import me.rytek.cardashboardclientv2.CarProtocol.SpotifyAddAction


class MainActivity : ComponentActivity() {

    val mConnectionManager = ConnectionManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup connection manager
        mConnectionManager.setContext(this)

        // Get device Info
        val deviceName = BluetoothAdapter.getDefaultAdapter().name

        val intentAction: IntentInformation

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                Log.d("MainActivity", "Intent Received")
                Log.d("MainActivity", "Type: ${intent.type}, Value: ${intent.getStringExtra(Intent.EXTRA_TEXT).toString()}")
                intentAction = getActionFromIntent(intent)
                if (intentAction.isValidAction) {
                    intentAction.action!!.sourceDevice = deviceName
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
                intentAction = IntentInformation(false)
            }
        }




        setContent {
            CarDashboardClientV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityComposable(mConnectionManager, intentAction)
//                    GlobalScope.launch {
//                        mConnectionManager.mqttConnect()
//                    }
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
fun MainActivityComposable(connectionManager: ConnectionManager, actionIntent: IntentInformation) {
    var state by remember { mutableStateOf("Not Connected") }

    connectionManager.onStateUpdate = { newState -> state = newState}

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Car Dashboard Client") }
        )

        Text(text = "State: $state")
        Box(modifier = Modifier.padding(vertical = 10.dp))
        Button(onClick = {
            GlobalScope.launch {
                connectionManager.mqttConnect()
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
                connectionManager.mqttDisconnect()
            }
        }) {
            Text("Disconnect")
        }

        Box(modifier = Modifier.padding(vertical = 10.dp))
        if (actionIntent.isValidAction) {
//            Text("Sharing: ${actionIntent.action!!.serialise()}")

            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                val painter = when (actionIntent.action!!.messageType) {
                    MessageType.SPOTIFY_PLAY, MessageType.SPOTIFY_ADD -> painterResource(R.drawable.spotify)
                    MessageType.LOCATION -> painterResource(R.drawable.maps)
                    MessageType.YOUTUBE ->painterResource(R.drawable.youtube)
                    else -> painterResource(R.drawable.chrome)
                }

                Image(
                    painter = painter,
                    contentDescription = "App Icon shared from"
                )

                Text(actionIntent.flavourText!!)
                Box(modifier = Modifier.padding(vertical = 5.dp))

                if (actionIntent.action.messageType === MessageType.SPOTIFY_PLAY) {
                    Button(onClick = {
                        GlobalScope.launch {
                            connectionManager.sendAction(actionIntent.action)
                        }
                    }) {
                        Text("Play now in Car")
                    }
                    Box(modifier = Modifier.padding(vertical = 5.dp))
                    Button(onClick = {
                        GlobalScope.launch {
                            val action = actionIntent.action as SpotifyPlayAction
                            val queueAction = SpotifyAddAction(action.sourceDevice, action.spotifyURI)
                            connectionManager.sendAction(queueAction)
                        }
                    }) {
                        Text("Add to Queue in Car")
                    }
                }
                else {

                }


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CarDashboardClientV2Theme {
        val action = parseSpotifyText("Here’s a song for you… Alemania by Twin Shadow\n" +
                "    https://open.spotify.com/track/5dkhXb9kA9TRhhC929wbkm?si=f6fh8WlQSkGPdYaBRQ4pgg&utm_source=native-share-menu&dl_branch=1")
        MainActivityComposable(ConnectionManager(), action)
    }
}