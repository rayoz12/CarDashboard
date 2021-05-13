package me.rytek.cardashboardclientv2

import android.content.Context
import me.rytek.cardashboardclientv2.ActionHandler
import me.rytek.cardashboardclientv2.CarProtocol.ActionInterface
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

    //MQTT settings
    private val mqttBroker = "tcp://maqiatto.com:1883"
    private val mqttUsername = "glass2k@ymail.com"
    private val mqttPassword = "CarDashboard"
    private val mqttTopic = "glass2k@ymail.com/CarDashboard"

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
            serverURI = mqttBroker,
            username = mqttUsername,
            password = mqttPassword,
            callback = this::handleMqttMessage
        )
        if (!success) {
            return success
        }
        return mqttInterface.subscribe(mqttTopic)
    }

    suspend fun mqttDisconnect(): Boolean {
        return mqttInterface.disconnect()
    }

    suspend fun sendAction(action: ActionInterface): Boolean {
        return mqttInterface.publish(mqttTopic, action.serialise())
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