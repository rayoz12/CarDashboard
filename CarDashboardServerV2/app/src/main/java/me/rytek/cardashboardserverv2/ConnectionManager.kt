package me.rytek.cardashboardserverv2

import android.content.Context
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets

/**
 * Handles Connecting over various Services (BT, Wifi, MQTT)
 *
 */
class ConnectionManager() {

    private var context: Context? = null

    private var actionHandler: ActionHandler? = null
    private val mqttInterface = MQTTInterface()

    fun setContext(newContext: Context) {
        context = newContext
        actionHandler = ActionHandler(newContext)
    }

    suspend fun mqttConnect(): Boolean {
        if (context == null) {
            throw Exception("Context is not defined! Call setContext(ctx)!")
        }

        val success = mqttInterface.connect(
            context!!,
            serverURI = "tcp://maqiatto.com:1883",
            username = "glass2k@ymail.com",
            password = "CarDashboard",
            callback = this::handleMqttMessage
        )
        if (!success) {
            return success
        }
        return mqttInterface.subscribe("glass2k@ymail.com/CarDashboard")
    }

    suspend fun mqttDisconnect(): Boolean {
        return mqttInterface.disconnect()
    }

    private fun handleMqttMessage(topic: String?, message: MqttMessage?) {
        if (message == null) return
        handleMessage(message.payload.toString(StandardCharsets.US_ASCII))
    }

    // Generic message handler
    private fun handleMessage(message: String) {
        if (context == null) {
            throw Exception("Context is not defined! Call setContext(ctx)!")
        }
        actionHandler!!.handleAction(message)
    }
}