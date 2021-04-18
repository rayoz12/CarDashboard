package me.rytek.cardashboardserverv2.CarProtocol

import android.util.Base64
import me.rytek.cardashboardserverv2.*
import java.nio.charset.StandardCharsets

// Gets the value of a message by looking at the 3rd position
fun getGenericValue(message: String): String {
    val parts: List<String> = message.split(",")
    return parts[2]
}

fun decodeSpotifyPlay(message: String): SpotifyPlayAction {
    val uri = getGenericValue(message)
    return SpotifyPlayAction(uri)
}

fun decodeSpotifyAdd(message: String): SpotifyAddAction {
    val uri = getGenericValue(message)
    return SpotifyAddAction(uri)
}

fun decodeYoutube(message: String): YoutubeAction {
    val url = getGenericValue(message)
    return YoutubeAction(url)
}

fun decodeWebPage(message: String): WebPageAction {
    val page = getGenericValue(message)
    return WebPageAction(page)
}

fun decodeLocation(message: String): LocationAction {
    val location = getGenericValue(message)
    val decodedBytes = Base64.decode(location, Base64.DEFAULT)
    val decodedString = decodedBytes.toString(StandardCharsets.UTF_8)
    return LocationAction(decodedString)
}