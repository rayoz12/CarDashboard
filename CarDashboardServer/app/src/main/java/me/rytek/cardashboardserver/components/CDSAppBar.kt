package me.rytek.cardashboardserver.components

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CDSAppBar() {
    TopAppBar(title = { Text(text = "Car Dashboard Server") })
}

@Composable
@Preview
fun CDSAppBarPreview() {
    TopAppBar(title = { Text(text = "Car Dashboard Server") })
}