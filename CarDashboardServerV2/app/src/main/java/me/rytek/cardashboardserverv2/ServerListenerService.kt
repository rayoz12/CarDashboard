package me.rytek.cardashboardserverv2

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServerListenerService : Service() {

    var state = "Not Connected"

    val connectionManager = ConnectionManager()

    // Binder given to clients
    private val binder = LocalBinder()
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): ServerListenerService = this@ServerListenerService
    }

    var onStateUpdatedCallback: ((newState: String) -> Unit)? = null
    fun onStateUpdated(callback: ((newState: String) -> Unit)) {
        onStateUpdatedCallback = callback
    }
    fun updateState(newState: String) {
        state = newState
        onStateUpdatedCallback?.invoke(newState)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connectionManager.setContext(this)
        mqttStart()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        connectionManager.setContext(this)
        return binder
    }

    override fun onDestroy() {
        mqttStop()
        super.onDestroy()
    }

    fun mqttStart() {
        GlobalScope.launch {
            updateState("Connecting...")
            val success = connectionManager.mqttConnect()
            updateState(if (success) "Connected & Subscribed" else "Connection Error")
        }
    }

    fun mqttStop() {
        GlobalScope.launch {
            updateState("Disconnecting...")
            val success = connectionManager.mqttDisconnect()
            updateState(if (success) "Not Connected" else "Failed to Disconnect!")
        }
    }
}