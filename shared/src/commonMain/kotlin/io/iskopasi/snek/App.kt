package io.iskopasi.snek

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iskopasi.snek.platform.GameUI
import io.iskopasi.snek.viewmodel.SnekViewModel

@Composable
@Preview
fun App(viewModel: SnekViewModel = viewModel { SnekViewModel(mapSize = 30) }) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            GameUI(viewModel = viewModel)
        }
    }
}
