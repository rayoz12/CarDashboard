package me.rytek.cardashboardserverv2

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ServerListenerService : Service() {

    val connectionManager = ConnectionManager()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connectionManager.setContext(this)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}