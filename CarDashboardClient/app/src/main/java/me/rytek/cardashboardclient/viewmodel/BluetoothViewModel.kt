package me.rytek.cardashboardclient.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothViewModel: ViewModel() {
    /**
     * Name of the connected device
     */
    private val mConnectedDeviceName = MutableLiveData<String?>(null)
    val connectedDeviceName: LiveData<String?> = mConnectedDeviceName

    /**
     * Array for the messages
     */
    private val mMessages = MutableLiveData<List<String>>(listOf())
    val messages: LiveData<List<String>> = mMessages

    /**
     * The status of the connection
     */
    private val mStatus = MutableLiveData<String?>(null)
    val status: LiveData<String?> = mStatus

    fun setConnectedDeviceName(name: String) {
        mConnectedDeviceName.value = name
    }

    fun setMessages(messages: List<String>) {
        mMessages.value = messages
    }

    fun clearMessages() {
        mMessages.value = listOf()
    }

    fun addMessage(message: String) {
        mMessages.value = messages.value!!.plus(message)
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    fun setStatus(subTitle: String) {
        mStatus.value = subTitle
    }

}