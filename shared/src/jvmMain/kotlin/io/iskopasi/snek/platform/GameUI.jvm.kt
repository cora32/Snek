package io.iskopasi.snek.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.snek.ui.Controls
import io.iskopasi.snek.ui.drawUI
import io.iskopasi.snek.ui.rememberCharacterImageBitmapMap
import io.iskopasi.snek.ui.rememberGeneralBitmapMap
import io.iskopasi.snek.viewmodel.SnekViewModel
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.stringResource
import snek.shared.generated.resources.Res
import snek.shared.generated.resources.apu
import snek.shared.generated.resources.score


@Composable
actual fun GameUI(modifier: Modifier, viewModel: SnekViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.W, Key.DirectionUp -> {
                            viewModel.moveUp()
                            true
                        }

                        Key.S, Key.DirectionDown -> {
                            viewModel.moveDown()
                            true
                        }

                        Key.A, Key.DirectionLeft -> {
                            viewModel.moveLeft()
                            true
                        }

                        Key.D, Key.DirectionRight -> {
                            viewModel.moveRight()
                            true
                        }

                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(Res.string.score, state.score),
            fontSize = 19.sp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        // Map container: Auto-scales to fit available space without overflowing
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val mapBoxSize = if (maxWidth < maxHeight) maxWidth else maxHeight
            // Res map
            val imageBitmapMap = rememberCharacterImageBitmapMap(imageNameMap = state.imageNameMap)
            val generalBitmapMap = rememberGeneralBitmapMap()

            Box(
                modifier = Modifier
                    .size(mapBoxSize)
                    .drawUI(
                        snekBody = state.snekBody,
                        foodPoints = state.foodPoints,
                        mapSize = state.mapSize,
                        generalBitmapMap = generalBitmapMap,
                        apuImage = imageResource(Res.drawable.apu),
                        imageBitmapMap = imageBitmapMap,
                        tileSettings = state.tileSettings
                    )
            )
        }

        Controls(
            modifier = Modifier.padding(vertical = 16.dp),
            onUp = viewModel::moveUp,
            onDown = viewModel::moveDown,
            onLeft = viewModel::moveLeft,
            onRight = viewModel::moveRight,
            onRestart = viewModel::onRestart
        )
    }
}