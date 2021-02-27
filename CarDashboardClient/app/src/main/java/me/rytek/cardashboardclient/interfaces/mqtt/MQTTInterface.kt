package me.rytek.cardashboardclient.interfaces.mqtt

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.rytek.cardashboardclient.interfaces.ConnectionInterface
import me.rytek.cardashboardclient.viewmodel.MQTTConfiguration
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTInterface(val context: Context): ConnectionInterface {

    override var isConnected: Boolean = false
        get() = this.mqttClient.isConnected

    private lateinit var mqttClient: MqttAndroidClient

    private lateinit var config: MQTTConfiguration


    companion object {
        const val TAG = "AndroidMqttClient"
    }

    private val _messageFlow = MutableSharedFlow<String>() // private mutable shared flow
    /**
     * Kotlin flow that emits when a new message is received.
     */
    override var onMessage = _messageFlow.asSharedFlow() //publicly exposed as read-only shared flow



    fun init(mqttConfig: MQTTConfiguration) {
        val serverURI = "tcp://maqiatto.com:1883"

        config = mqttConfig

        mqttClient = MqttAndroidClient(context, mqttConfig.address, "CarDashboard${(0..100).random()}")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        });
    }


    /**
     * Connect and start listening for messages
     */
    override fun start() {
        val options = MqttConnectOptions()
        options.userName = "glass2k@ymail.com"
        options.password = "CarDashboard".toCharArray()
        options.isAutomaticReconnect = true

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * Stop Listening for messages
     */
    override fun stop() {
        mqttClient.disconnect()
    }

    /**
     * Stop and clean up the class
     */
    override fun destroy() {
        TODO("Not yet implemented")
    }

    /**
     * Send String data across the interface
     *
     * @param data
     */
    override fun send(data: String) {
        mqttClient.publish(config.topic, MqttMessage(data.toByteArray()))
    }
}