package me.rytek.cardashboardclient.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {

    /**
     * Flag which determines if this has already been initalialised.
     * Means that multiple calls to init are ignored (such as when an activity is recreated)
     */
    private var isInitialised = false

    private lateinit var store: DataStore<Preferences>

    companion object PreferenceKeys {
        val WIFI_ADDRESS = stringPreferencesKey("wifi_address")
        val WIFI_PORT = intPreferencesKey("wifi_port")
        val BT_NAME = stringPreferencesKey("bt_name")
        val BT_ADDRESS = stringPreferencesKey("bt_hardwareAddress")
        val MQTT_ADDRESS = stringPreferencesKey("mqtt_address")
        val MQTT_TOPIC = stringPreferencesKey("mqtt_topic")
    }

    fun initFromDataStore(store: DataStore<Preferences>) {
        if (isInitialised)
            return

        this.store = store;

        GlobalScope.launch {
            store.data.collect {
                val wifiAddress = it[WIFI_ADDRESS]
                val wifiPort = it[WIFI_PORT]
                val btName = it[BT_NAME]
                val btAddress = it[BT_ADDRESS]
                val mqttAddress = it[MQTT_ADDRESS]
                val mqttTopic = it[MQTT_TOPIC]

                // Detect if stuff has changed and update configuration
                var wifiChanged = false
                var BTChanged = false
                var mqttChanged = false

                // Wifi
                if (wifiAddress != null) {
                    if (wifiAddress != mServerAddress.value) {
                        mServerAddress.postValue(wifiAddress)
                        wifiChanged = true;
                    }
                }
                if (wifiPort != null) {
                    if (wifiPort != mServerPort.value) {
                        mServerPort.postValue(wifiPort)
                        wifiChanged = true;
                    }
                }
                // BT
                if (btName != null) {
                    if (btName != mDeviceName.value) {
                        mDeviceName.postValue(btName)
                        BTChanged = true
                    }
                }
                if (btAddress != null) {
                    if (btAddress != mDeviceHardwareAddress.value) {
                        mDeviceHardwareAddress.postValue(btAddress)
                        BTChanged = true
                    }
                }
                //MQTT
                if (mqttAddress != null) {
                    if (mqttAddress != mBrokerAddress.value) {
                        mBrokerAddress.postValue(mqttAddress)
                        mqttChanged = true
                    }
                }

                if (mqttTopic != null) {
                    if (mqttTopic != mTopic.value) {
                        mTopic.postValue(mqttTopic)
                        mqttChanged = true
                    }
                }


                if (wifiChanged) {
                    mWifiConfig.value = WifiConfiguration(wifiAddress, wifiPort)
                }
                if (BTChanged) {
                    mBTConfig.value = BTConfiguration(btName, btAddress)
                }
                if (mqttChanged) {
                    Log.d("SettingsViewModel", MQTTConfiguration(mqttAddress, mqttTopic).toString())
                    mMQTTConfig.value = MQTTConfiguration(mqttAddress, mqttTopic)
                }
            }
        }
        isInitialised = true
    }

    fun <T> writeStore(key: Preferences.Key<T>, data: T) {
        GlobalScope.launch {
            store.edit { settings ->
                settings[key] = data
            }
        }
    }


    // Wifi
    /**
     * Address of server
     */
    private val mServerAddress = MutableLiveData<String?>(null)
    val serverAddress: LiveData<String?> = mServerAddress

    /**
     * Port of server
     */
    private val mServerPort = MutableLiveData<Int?>(null)
    val serverPort: LiveData<Int?> = mServerPort

    fun setServerAddress(addr: String) {
        mServerAddress.value = addr
        writeStore(WIFI_ADDRESS, addr)
    }

    fun setServerPort(port: Int) {
        mServerPort.value = port
        writeStore(WIFI_PORT, port)
    }

    // Bluetooth
    /**
     * Device Name
     */
    private val mDeviceName = MutableLiveData<String?>(null)
    val deviceName: LiveData<String?> = mDeviceName

    /**
     * Hardware MAC of device
     */
    private val mDeviceHardwareAddress = MutableLiveData<String?>(null)
    val deviceHardwareAddress: LiveData<String?> = mDeviceHardwareAddress

    fun setDeviceName(name: String) {
        mDeviceName.value = name
        writeStore(BT_NAME, name)
    }

    fun setDeviceHardwareAddress(addr: String) {
        mDeviceHardwareAddress.value = addr
        writeStore(BT_ADDRESS, addr)
    }

    // MQTT
    /**
     * Address of broker to subscribe to
     */
    private val mBrokerAddress = MutableLiveData<String?>(null)
    val brokerAddress: LiveData<String?> = mBrokerAddress

    private val mTopic = MutableLiveData<String?>(null)
    val topic: LiveData<String?> = mTopic

    fun setBrokerAddress(addr: String) {
        mBrokerAddress.value = addr
        writeStore(MQTT_ADDRESS, addr)
    }

    fun setTopic(topic: String) {
        mTopic.value = topic
        writeStore(MQTT_TOPIC, topic)
    }

    // Combined Settings
    private val mWifiConfig = MutableStateFlow(WifiConfiguration(null, null))
    val wifiConfig: StateFlow<WifiConfiguration> = mWifiConfig

    private val mBTConfig = MutableStateFlow(BTConfiguration(null, null))
    val BTConfig: StateFlow<BTConfiguration> = mBTConfig

    private val mMQTTConfig = MutableStateFlow(MQTTConfiguration("tcp://maqiatto.com:1883", "glass2k@ymail.com/CarDashboard"))
    val MQTTConfig: StateFlow<MQTTConfiguration> = mMQTTConfig

}

data class WifiConfiguration(val address: String?, val port: Int?)
data class BTConfiguration(val name: String?, val address: String?)
data class MQTTConfiguration(val address: String?, val topic: String?)