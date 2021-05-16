package me.rytek.cardashboardclientv2

import android.content.Intent
import android.util.Log
import me.rytek.cardashboardclientv2.CarProtocol.ActionInterface
import me.rytek.cardashboardclientv2.CarProtocol.SpotifyPlayAction

data class IntentInformation(val isValidAction: Boolean, val flavourText: String? = null, val action: ActionInterface? = null)

fun spotifyURLToURI(url: String): String {
    var uri = url.replace("https://open.spotify.com", "spotify")
    uri = uri.replace("/", ":")
    return uri
}

fun parseSpotifyText(sharedMessage: String): IntentInformation {
    // Parse URL
    // get url only
    val urlLocation = sharedMessage.indexOf("http")
    var url = sharedMessage.drop(urlLocation)
    val urlEnd = url.indexOf("?si=")
    if (urlEnd != -1) {
        url = url.substring(0, urlEnd)
    }
    val uri = spotifyURLToURI(url)

    // Get Flavour Text
    // Get track info
    val start = sharedMessage.indexOf("…")
    val end = sharedMessage.indexOf("\n")
    val trackInfo = sharedMessage.substring(start + 2, end)
    // Get type
    val type = uri.split(":")[1].capitalize()

    val flavourText = "$type: $trackInfo"

    val action = SpotifyPlayAction("Source", uri)

    return IntentInformation(true, flavourText, action)
}

fun getActionFromIntent(intent: Intent): IntentInformation {

    when (intent.type) {
        "text/plain" -> {
            // Get string and parse it
            val sharedMessage = intent.getStringExtra(Intent.EXTRA_TEXT).toString()
            if (sharedMessage.contains("open.spotify.com")) {
                parseSpotifyText(sharedMessage)
            }
            else {
                Log.d("IntentHandler", "Unknown shared message")
            }
        }
    }

    return IntentInformation(false)
}

