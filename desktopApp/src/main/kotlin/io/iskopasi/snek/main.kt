package io.iskopasi.snek

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 1200.dp,
        height = 1200.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Snek",
    ) {
        App()
    }
}