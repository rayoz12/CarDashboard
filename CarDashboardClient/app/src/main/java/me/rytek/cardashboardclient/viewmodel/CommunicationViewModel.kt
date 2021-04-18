package me.rytek.cardashboardclient.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import me.rytek.cardashboardclient.services.CommunicationService

class CommunicationViewModel: ViewModel() {

    var commsService: CommunicationService? = null

    private var isInitialised = false

    fun init(settingsViewModel: SettingsViewModel, applicationCtx: Context) {
        if (isInitialised)
            return

        commsService = CommunicationService(settingsViewModel, applicationCtx)
        isInitialised = true
    }
}