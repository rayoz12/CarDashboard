package me.rytek.cardashboardclient.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.rytek.cardashboardclient.viewmodel.CommunicationViewModel

@Preview
@Composable
fun DebugScreenPreview() {
    DebugScreen(CommunicationViewModel())
}

@Composable
fun DebugScreen(communicationViewModel: CommunicationViewModel) {

    Column {
        Spacer(Modifier.height(16.dp))
        Text("Spotify", style = MaterialTheme.typography.h3)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                communicationViewModel.commsService?.sendString("#,spotify-play,spotify:track:6rqhFgbbKwnb9MLmUQDhG6,\n")
            }) {
                Text(text = "Play Track")
            }
            Button(onClick = {
                communicationViewModel.commsService?.sendString("#,spotify-play,spotify:album:4m2880jivSbbyEGAKfITCa,\n")
            }) {
                Text(text = "Play Album")
            }
            Button(onClick = {
                communicationViewModel.commsService?.sendString("#,spotify-play,spotify:playlist:2q17dd27EffL3oiaicTRYS,\n")
            }) {
                Text(text = "Play Playlist")
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Others", style = MaterialTheme.typography.h3)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                communicationViewModel.commsService?.sendString("#,location,geo:-33.746887,150.828035?q=19%20Tabitha%20Pl,%20Plumpton%20NSW%202761,\n")
            }) {
                Text(text = "Location")
            }
            Button(onClick = {}) {
                Text(text = "Youtube")
            }
            Button(onClick = {}) {
                Text(text = "Web Page")
            }
        }
    }
}