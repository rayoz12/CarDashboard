package me.rytek.cardashboardclientv2.CarProtocol

import android.util.Base64
import java.nio.charset.StandardCharsets


class SpotifyPlayAction(override var sourceDevice: String, val spotifyURI: String) : ActionInterface {
    override var messageType = MessageType.SPOTIFY_PLAY
    override fun serialise(): String {
        return "#,$sourceDevice,spotify-play,$spotifyURI,\n"
    }
}

class SpotifyAddAction(override var sourceDevice: String, val spotifyURI: String) : ActionInterface {
    override var messageType = MessageType.SPOTIFY_ADD
    override fun serialise(): String {
        return "#,$sourceDevice,spotify-add,$spotifyURI,\n"
    }
}

class YoutubeAction(override var sourceDevice: String, val youtubeURI: String) : ActionInterface {
    override var messageType = MessageType.YOUTUBE
    override fun serialise(): String {
        return "#,$sourceDevice,youtube,$youtubeURI,\n"
    }
}

class WebPageAction(override var sourceDevice: String, val url: String) : ActionInterface {
    override var messageType = MessageType.WEB_PAGE
    override fun serialise(): String {
        return "#,$sourceDevice,url,$url,\n"
    }
}

class LocationAction(override var sourceDevice: String, val location: String) : ActionInterface {
    override var messageType = MessageType.LOCATION
    override fun serialise(): String {
        val encodedString = Base64.encodeToString(location.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
        return "#,$sourceDevice,location,$encodedString,\n"
    }
}