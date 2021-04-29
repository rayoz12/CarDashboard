package me.rytek.cardashboardserverv2

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var mService: ServerListenerService? = null
    var mBound: Boolean = false

    private val _state = MutableLiveData("Not Connected")
    val state: LiveData<String> = _state


    fun setService(service: ServerListenerService) {
        mService = service
        mService?.onStateUpdated {
            _state.postValue(it)
        }
        mBound = true
    }

    fun clearService() {
        mService = null
        mBound = false
    }

}