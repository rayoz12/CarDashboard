package me.rytek.cardashboardserverv2

import android.content.Context
import android.util.Log
import me.rytek.cardashboardserverv2.CarProtocol.*
import androidx.core.content.ContextCompat.startActivity

import android.net.Uri

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK


/**
 * This class takes in actions, decodes them and dispatches it to Android
 *
 */
class ActionHandler(private val context: Context) {

    /**
     * Checks if the message is formatted properly
     * Starts with a # has an action and a value (it doesn't care if they are valid actions)
     *
     * @param message string of action
     * @return
     */
    fun isValidMessage(message: String): Boolean {
        if (message[0] != '#') return false
        if (message[1] != ',') return false
        val parts: List<String> = message.split(",")
        if (parts.size < 3) return false

        return true
    }

    fun handleAction(message: String) {
        if (!isValidMessage(message)) return
        val parts: List<String> = message.split(",")
        val actionType = parts[1]

        // Get the message
        val action: ActionInterface = when (actionType) {
            "spotify-play"  -> decodeSpotifyPlay(message)
            "spotify-add"   -> decodeSpotifyAdd(message)
            "youtube"       -> decodeYoutube(message)
            "url"           -> decodeWebPage(message)
            "location"      -> decodeLocation(message)
            else -> {
                Log.e("ActionHandler", "Unknown Message!")
                return
            }
        }
        try {
            // Dispatch the message
            when (action.messageType) {
                MessageType.SPOTIFY_PLAY -> {
                    val uri = (action as SpotifyPlayAction).spotifyURI
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    launcher.flags = FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, launcher, null)
                }
                MessageType.SPOTIFY_ADD -> {
                    Log.e("ActionHandler", "Can't Dispatch Add to Queue")
                }
                MessageType.YOUTUBE -> {
                    val uri = (action as YoutubeAction).youtubeURI
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    launcher.flags = FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, launcher, null)
                }
                MessageType.WEB_PAGE -> {
                    val uri = (action as WebPageAction).url
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    launcher.flags = FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, launcher, null)
                }
                MessageType.LOCATION -> {
                    val uri = (action as LocationAction).location
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    launcher.flags = FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, launcher, null)
                }
            }
        }
        catch (e: Exception) {
            Log.e("ActionHandler", "Failed to run: $e")
        }
    }
}