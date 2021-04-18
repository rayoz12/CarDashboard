package me.rytek.cardashboardserverv2.CarProtocol

enum class MessageType {
    SPOTIFY_PLAY,
    SPOTIFY_ADD,
    YOUTUBE,
    WEB_PAGE,
    LOCATION
}

interface ActionInterface {
    val messageType: MessageType
    fun serialise(): String
}