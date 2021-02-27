package me.rytek.cardashboardclient.interfaces

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import me.rytek.cardashboardclient.viewmodel.SettingsViewModel

interface ConnectionInterface {

    /**
     * Initialise the Interface.
     * Includes Getting references to real hardware and preparing the class
     */
    // fun init(settings: SettingsViewModel)

    /**
     * Connect and start listening for messages
     */
    fun start()

    /**
     * Stop Listening for messages
     */
    fun stop()

    /**
     * Stop and clean up the class
     */
    fun destroy()

    /**
     * Send String data across the interface
     *
     * @param data
     */
    fun send(data: String)

    /**
     * Kotlin flow that emits when a new message is received.
     */
    var onMessage: SharedFlow<String>

    /**
     * If this interface is connected
     */
    var isConnected: Boolean
}