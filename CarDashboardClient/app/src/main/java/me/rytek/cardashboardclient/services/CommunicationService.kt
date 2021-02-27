package me.rytek.cardashboardclient.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rytek.cardashboardclient.interfaces.mqtt.MQTTInterface
import me.rytek.cardashboardclient.interfaces.wifi.WifiInterface
import me.rytek.cardashboardclient.viewmodel.BTConfiguration
import me.rytek.cardashboardclient.viewmodel.MQTTConfiguration
import me.rytek.cardashboardclient.viewmodel.SettingsViewModel
import me.rytek.cardashboardclient.viewmodel.WifiConfiguration

/**
 * This Handles communicating with the server over any method.
 * We can connect over any method and send a command.
 * Self Contained by listening to configuration changes and reconnecting
 */
class CommunicationService(settingsViewModel: SettingsViewModel, private val applicationCtx: Context) {

    private var wifiConfig = WifiConfiguration(null, null)
    private var btConfig = BTConfiguration(null, null)
    private var mqttConfig = MQTTConfiguration(null, null)

    private val wifiInterface = WifiInterface()
    private val mqttInterface = MQTTInterface(applicationCtx)

    init {
        // Set up observers on the view model
        GlobalScope.launch {
            settingsViewModel.wifiConfig.collect {
                wifiConfig = it
                if (wifiInterface.isConnected) {
                    wifiDisconnect()
                    wifiConnect()
                }
            }
            settingsViewModel.BTConfig.collect {
                btConfig = it
            }
            settingsViewModel.MQTTConfig.collect {
                mqttConfig = it
                if (mqttInterface.isConnected) {
                    mqttDisconnect()
                    mqttConnect()
                }
            }
        }
    }

    fun wifiToggle() {
        if (wifiInterface.isConnected) {
            wifiDisconnect()
        }
        else {
            wifiConnect()
        }
    }

    /**
     * Connect on the wifi interface
     *
     * @return success
     */
    fun wifiConnect(): Boolean {
        if (wifiConfig.address == null || wifiConfig.port == null) {
            return false
        }
        wifiInterface.init(wifiConfig)
        wifiInterface.start()
        return true
    }

    fun wifiDisconnect() {
        wifiInterface.stop()
    }

    fun mqttToggle() {
        if (mqttInterface.isConnected) {
            mqttDisconnect()
        }
        else {
            mqttConnect()
        }
    }

    fun mqttConnect(): Boolean {
        if (mqttConfig.address == null || mqttConfig.topic == null) {
            return false
        }
        mqttInterface.init(mqttConfig)
        mqttInterface.start()
        return true
    }

    fun mqttDisconnect() {
        mqttInterface.stop()
    }



}