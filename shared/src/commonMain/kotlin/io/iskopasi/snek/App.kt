package io.iskopasi.snek

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iskopasi.snek.model.Point
import io.iskopasi.snek.viewmodel.SnekViewModel
import org.jetbrains.compose.resources.stringResource
import snek.shared.generated.resources.Res
import snek.shared.generated.resources.score
import kotlin.math.min

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

@Composable
fun GameUI(modifier: Modifier = Modifier, viewModel: SnekViewModel) {
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
            Box(
                modifier = Modifier
                    .size(mapBoxSize)
                    .drawUI(
                        snekBody = state.snekBody,
                        foodPoints = state.foodPoints,
                        mapSize = state.mapSize
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

@Composable
fun Controls(
    modifier: Modifier = Modifier,
    onUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onRestart: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onRestart
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            modifier = modifier then Modifier.size(150.dp)
                .background(
                    color = Color(0xff444444),
                    CircleShape
                )
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.TopCenter),
                onClick = onUp
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Up",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onRight
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Right",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = onDown
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Down",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onLeft
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Left",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun Modifier.drawUI(
    snekBody: List<Point>,
    foodPoints: Set<Point>,
    mapSize: Int
): Modifier {
    val currentSnekBody by rememberUpdatedState(snekBody)
    val currentFoodPoints by rememberUpdatedState(foodPoints)
    val currentMapSize by rememberUpdatedState(mapSize)

    return this.drawWithContent {
        val step = min(size.height, size.width) / currentMapSize
        val netBrush = SolidColor(Color(0xffffffff))
        val snekBrush = SolidColor(Color(0xff33aa33))
        val brushHead = SolidColor(Color(0xff22ff22))
        val foodBrush = SolidColor(Color(0xffff2222))

        drawRect(color = Color(0xFF121212))

        drawNet(brush = netBrush, mapSize = currentMapSize, step = step)

        drawFood(
            brush = foodBrush,
            foodPoints = currentFoodPoints,
            step = step
        )

        drawSnek(
            brushBody = snekBrush,
            brushHead = brushHead,
            snekBody = currentSnekBody,
            step = step
        )
    }
}

fun ContentDrawScope.drawSnek(
    brushBody: SolidColor,
    brushHead: SolidColor,
    snekBody: List<Point>,
    step: Float,
) {
    val radius = step / 3
    val offset = step / 2f

    for (i in snekBody.size - 1 downTo 0) {
        val segment = snekBody[i]

        drawCircle(
            brush = if (i == 0) brushHead else brushBody,
            radius = radius,
            center = Offset(
                offset + segment.x * step,
                offset + segment.y * step
            )
        )
    }
}

fun ContentDrawScope.drawFood(
    brush: SolidColor,
    foodPoints: Set<Point>,
    step: Float,
) {
    val radius = step / 3
    val offset = step / 2f

    for (food in foodPoints) {
        drawCircle(
            brush = brush,
            radius = radius,
            center = Offset(
                offset + food.x * step,
                offset + food.y * step
            )
        )
    }
}

fun ContentDrawScope.drawNet(brush: SolidColor, mapSize: Int, step: Float) {
    for (i in 0..mapSize) {
        // horizontal line
        drawLine(
            brush = brush,
            start = Offset(0f, i * step),
            end = Offset(size.width, i * step),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // vertical line
        drawLine(
            brush = brush,
            start = Offset(i * step, 0f),
            end = Offset(i * step, size.height),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}