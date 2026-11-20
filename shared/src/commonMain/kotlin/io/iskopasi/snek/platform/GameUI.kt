package io.iskopasi.snek.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.iskopasi.snek.viewmodel.SnekViewModel

@Composable
expect fun GameUI(modifier: Modifier = Modifier, viewModel: SnekViewModel)