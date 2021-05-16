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

    private var state = "Not Connected"

    var onStateUpdate: ((newState: String) -> Unit)? = null

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

        if (state == "Connecting..." || state == "Connected. Subscribing..." || state == "Subscribed") {
            return false;
        }

        onStateUpdate?.invoke("Connecting...")

        val success = mqttInterface.connect(
            context!!,
            serverURI = mqttBroker,
            username = mqttUsername,
            password = mqttPassword,
            callback = this::handleMqttMessage
        )
        if (!success) {
            onStateUpdate?.invoke("Connection Error")
            return success
        }
        onStateUpdate?.invoke("Connected. Subscribing...")

        return if (mqttInterface.subscribe(mqttTopic)) {
            onStateUpdate?.invoke("Subscribed")
            true
        } else {
            onStateUpdate?.invoke("Failed to Subscribe")
            false
        }

    }

    suspend fun mqttDisconnect(): Boolean {

        if (state == "Disconnecting..." || state == "Disconnected" || state == "Not Connected") {
            return false
        }

        onStateUpdate?.invoke("Disconnecting...")
        return if (mqttInterface.disconnect()) {
            onStateUpdate?.invoke("Disconnected")
            true
        }
        else {
            onStateUpdate?.invoke("Failed to Disconnect")
            false
        }
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