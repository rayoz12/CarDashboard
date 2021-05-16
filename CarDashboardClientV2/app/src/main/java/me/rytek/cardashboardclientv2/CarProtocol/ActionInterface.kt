package me.rytek.cardashboardclientv2.CarProtocol

enum class MessageType {
    SPOTIFY_PLAY,
    SPOTIFY_ADD,
    YOUTUBE,
    WEB_PAGE,
    LOCATION
}

interface ActionInterface {
    var sourceDevice: String
    var messageType: MessageType
    fun serialise(): String
}