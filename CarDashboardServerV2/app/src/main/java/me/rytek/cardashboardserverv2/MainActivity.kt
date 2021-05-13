package me.rytek.cardashboardserverv2

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.rytek.cardashboardserverv2.ui.theme.CarDashboardServerV2Theme
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.startActivityForResult
import android.annotation.TargetApi
import android.app.Activity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : ComponentActivity() {
    val viewModel: MainActivityViewModel = MainActivityViewModel()


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ServerListenerService.LocalBinder
            viewModel.setService(binder.getService())
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            viewModel.clearService()
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (!Settings.canDrawOverlays(this)) {
            // You don't have permission
            checkPermission()
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please grant permission to appear on top, So we can launch applications", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startForResult.launch(intent)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to LocalService
        Intent(this, ServerListenerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }



        setContent {
            CarDashboardServerV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityComposable(viewModel)
                }
            }
        }

        checkPermission()
    }



}

@Composable
fun MainActivityComposable(viewModel: MainActivityViewModel) {
    val context = LocalContext.current
    val state: String by viewModel.state.observeAsState("Not Connected")
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Car Dashboard Server") }
        )

        Text(text = "State: $state")
        Box(modifier = Modifier.padding(vertical = 10.dp))
        Button(onClick = {
            GlobalScope.launch {
                if (viewModel.mService == null) {
                    Log.e("MainActivityComposeable", "Listener is null!")
                }
                else {
                    viewModel.mService?.mqttStart()
                }

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
                if (viewModel.mService == null) {
                    Log.e("MainActivityComposeable", "Listener is null!")
                }
                else {
                    viewModel.mService?.mqttStop()
                }
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
        MainActivityComposable(viewModel = MainActivityViewModel())
    }
}