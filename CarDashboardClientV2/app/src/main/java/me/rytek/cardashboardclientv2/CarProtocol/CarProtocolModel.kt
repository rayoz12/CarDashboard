package me.rytek.cardashboardclientv2.CarProtocol

import android.util.Base64
import java.nio.charset.StandardCharsets


class SpotifyPlayAction(val spotifyURI: String) : ActionInterface {
    override val messageType = MessageType.SPOTIFY_PLAY
    override fun serialise(): String {
        return "#,spotify-play,$spotifyURI,\n"
    }
}

class SpotifyAddAction(val spotifyURI: String) : ActionInterface {
    override val messageType = MessageType.SPOTIFY_ADD
    override fun serialise(): String {
        return "#,spotify-add,$spotifyURI,\n"
    }
}

class YoutubeAction(val youtubeURI: String) : ActionInterface {
    override val messageType = MessageType.YOUTUBE
    override fun serialise(): String {
        return "#,youtube,$youtubeURI,\n"
    }
}

class WebPageAction(val url: String) : ActionInterface {
    override val messageType = MessageType.WEB_PAGE
    override fun serialise(): String {
        return "#,url,$url,\n"
    }
}

class LocationAction(val location: String) : ActionInterface {
    override val messageType = MessageType.LOCATION
    override fun serialise(): String {
        val encodedString = Base64.encodeToString(location.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
        return "#,location,$encodedString,\n"
    }
}