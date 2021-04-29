package me.rytek.cardashboardserverv2

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start

class MQTTInterface {
    private lateinit var mqttClient: MqttAndroidClient
    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }

    suspend fun connect(
        context: Context,
        serverURI: String,
        username: String? = null,
        password: String? = null,
        callback: ((topic: String?, message: MqttMessage?) -> Unit)? = null
    ): Boolean =
        // val serverURI = "tcp://maqiatto.com:1883"
        suspendCoroutine { cont ->
            mqttClient = MqttAndroidClient(context, serverURI, "CarDashboardServer" + (0..100).random())
            mqttClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
                    callback?.invoke(topic, message)
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                }
            })
            val options = MqttConnectOptions()
            if (username != null && password != null) {
                options.userName = username
                options.password = password.toCharArray()
            }
            try {
                mqttClient.connect(options, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(TAG, "Connection success")
                        cont.resume(true)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Connection failure: $exception")
                        cont.resume(false)
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
                cont.resume(false)
            }
        }

    suspend fun subscribe(topic: String, qos: Int = 1): Boolean =
        suspendCoroutine { cont ->
            try {
                mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(TAG, "Subscribed to $topic")
                        cont.resume(true)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to subscribe $topic: $exception")
                        cont.resume(false)
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
                cont.resume(false)
            }
        }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    suspend fun disconnect(): Boolean =
        suspendCoroutine { cont ->
            if (!mqttClient.isConnected) {
                cont.resume(true)
            }
            try {
                mqttClient.disconnect(null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(TAG, "Disconnected")
                        cont.resume(true)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to disconnect")
                        cont.resume(false)
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
                cont.resume(false)
            }
        }
}