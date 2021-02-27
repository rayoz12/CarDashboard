package me.rytek.cardashboardclient.interfaces.wifi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import me.rytek.cardashboardclient.interfaces.ConnectionInterface
import me.rytek.cardashboardclient.viewmodel.SettingsViewModel
import me.rytek.cardashboardclient.viewmodel.WifiConfiguration
import java.lang.IllegalArgumentException

class WifiInterface: ConnectionInterface {

    lateinit var client: TCPClient;

    /**
     * Kotlin flow that emits when a new message is received.
     */
    override lateinit var onMessage: SharedFlow<String>

    override var isConnected = false;

    /**
     * Initialise the Interface.
     * Includes Getting references to real hardware and preparing the class
     */
    fun init(wifiConfig: WifiConfiguration) {
        // Read config
        // Connect to the server
        if (wifiConfig.address != null && wifiConfig.port != null) {
            client = TCPClient(wifiConfig.address, wifiConfig.port)
        }
        else {
            throw IllegalArgumentException("Settings doesn't provide valid values for serverAddress & serverPort")
        }
        // setup flow
        onMessage = client.onMessageEvent
    }

    /**
     * Connect and start listening for messages
     */
    override fun start() {
        // connect
        client.connect()
    }

    /**
     * Stop Listening for messages
     */
    override fun stop() {
        client.stop()
    }

    /**
     * Stop and clean up the class
     */
    override fun destroy() {
        stop()
    }

    /**
     * Send String data across the interface
     *
     * @param data
     */
    override fun send(data: String) {
        client.send(data)
    }
}