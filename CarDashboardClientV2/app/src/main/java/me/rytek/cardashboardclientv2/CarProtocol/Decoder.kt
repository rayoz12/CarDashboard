package me.rytek.cardashboardclientv2.CarProtocol

import android.util.Base64
import java.nio.charset.StandardCharsets

// Gets the source device of a message by looking at the 2nd position
fun getSourceDevice(message: String): String {
    val parts: List<String> = message.split(",")
    return parts[1]
}

// Gets the value of a message by looking at the 4th position
fun getMessageValue(message: String): String {
    val parts: List<String> = message.split(",")
    return parts[3]
}

fun decodeSpotifyPlay(message: String): SpotifyPlayAction {
    val sourceDevice = getSourceDevice(message)
    val uri = getMessageValue(message)
    return SpotifyPlayAction(sourceDevice, uri)
}

fun decodeSpotifyAdd(message: String): SpotifyAddAction {
    val sourceDevice = getSourceDevice(message)
    val uri = getMessageValue(message)
    return SpotifyAddAction(sourceDevice, uri)
}

fun decodeYoutube(message: String): YoutubeAction {
    val sourceDevice = getSourceDevice(message)
    val url = getMessageValue(message)
    return YoutubeAction(sourceDevice, url)
}

fun decodeWebPage(message: String): WebPageAction {
    val sourceDevice = getSourceDevice(message)
    val page = getMessageValue(message)
    return WebPageAction(sourceDevice, page)
}

fun decodeLocation(message: String): LocationAction {
    val sourceDevice = getSourceDevice(message)
    val location = getMessageValue(message)
    val decodedBytes = Base64.decode(location, Base64.DEFAULT)
    val decodedString = decodedBytes.toString(StandardCharsets.UTF_8)
    return LocationAction(sourceDevice, decodedString)
}