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
fun DebugScreen() {

    val communicationViewModel: CommunicationViewModel = viewModel()
    val commsService = communicationViewModel.commsService

    Column {
        Spacer(Modifier.preferredHeight(16.dp))
        Text("Spotify", style = MaterialTheme.typography.h3)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {}) {
                Text(text = "Play Track")
            }
            Button(onClick = {}) {
                Text(text = "Play Album")
            }
            Button(onClick = {}) {
                Text(text = "Play Playlist")
            }
        }
        Spacer(Modifier.preferredHeight(16.dp))
        Text("Others", style = MaterialTheme.typography.h3)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {}) {
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