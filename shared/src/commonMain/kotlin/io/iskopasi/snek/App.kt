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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iskopasi.snek.model.Point
import io.iskopasi.snek.model.Segment
import io.iskopasi.snek.utils.loadBitmapByName
import io.iskopasi.snek.viewmodel.SnekViewModel
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.stringResource
import snek.shared.generated.resources.Res
import snek.shared.generated.resources.apu
import snek.shared.generated.resources.grass_01
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
            // Res map
            val imageBitmapMap = rememberImageBitmapMap(imageNameMap = state.imageNameMap)

            Box(
                modifier = Modifier
                    .size(mapBoxSize)
                    .drawUI(
                        snekBody = state.snekBody,
                        foodPoints = state.foodPoints,
                        mapSize = state.mapSize,
                        grassImage = imageResource(Res.drawable.grass_01),
                        apuImage = imageResource(Res.drawable.apu),
                        imageBitmapMap = imageBitmapMap
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
fun rememberImageBitmapMap(imageNameMap: Map<Int, String>): Map<Int, ImageBitmap> {
    val bitmapMapState = produceState<Map<Int, ImageBitmap>>(
        initialValue = emptyMap(),
        keys = arrayOf(imageNameMap)
    ) {
        value = imageNameMap.mapNotNull { (key, name) ->
            val bitmap = loadBitmapByName(name)
            if (bitmap != null) key to bitmap else null
        }.toMap()
    }
    return bitmapMapState.value
}

@Composable
fun Modifier.drawUI(
    snekBody: List<Segment>,
    foodPoints: Set<Point>,
    mapSize: Int,
    grassImage: ImageBitmap,
    apuImage: ImageBitmap,
    imageBitmapMap: Map<Int, ImageBitmap>
): Modifier {
    val currentSnekBody by rememberUpdatedState(snekBody)
    val currentFoodPoints by rememberUpdatedState(foodPoints)
    val currentMapSize by rememberUpdatedState(mapSize)

    return this.drawWithCache {
        val step = min(size.height, size.width) / currentMapSize
        val netBrush = SolidColor(Color(0x22000000))
        val snekBrush = SolidColor(Color(0xff33aa33))
        val brushHead = SolidColor(Color(0xff22ff22))
        val foodBrush = SolidColor(Color(0xffff2222))

        onDrawWithContent {
            drawRect(color = Color(0xFF121212))

            drawNet(
                brush = netBrush,
                grassImage = grassImage,
                mapSize = currentMapSize, step = step
            )

            drawFood(
                brush = foodBrush,
                foodPoints = currentFoodPoints,
                step = step,
                apuImage = apuImage
            )

            drawSnek(
                brushBody = snekBrush,
                brushHead = brushHead,
                snekBody = currentSnekBody,
                step = step,
                imageBitmapMap = imageBitmapMap
            )
        }

    }
}

fun ContentDrawScope.drawSnek(
    brushBody: SolidColor,
    brushHead: SolidColor,
    snekBody: List<Segment>,
    step: Float,
    imageBitmapMap: Map<Int, ImageBitmap>
) {
    val radius = step / 3
    val offset = step / 2f

    for (i in snekBody.size - 1 downTo 0) {
        val segment = snekBody[i]
        val image = imageBitmapMap[i]


        if (image == null) {
            drawCircle(
                brush = if (i == 0) brushHead else brushBody,
                radius = radius,
                center = Offset(
                    offset + segment.point.x * step,
                    offset + segment.point.y * step
                )
            )
        } else {
            val row = segment.point.y
            val col = segment.point.x

            val top = (row * step).toInt()
            val bottom = ((row + 1) * step).toInt()
            val tileHeight = bottom - top

            val left = (col * step).toInt()
            val right = ((col + 1) * step).toInt()
            val tileWidth = right - left

            drawImage(
                image = image,
                dstOffset = IntOffset(left, top),
                dstSize = IntSize(tileWidth, tileHeight)
            )
        }
    }
}

fun ContentDrawScope.drawFood(
    brush: SolidColor,
    foodPoints: Set<Point>,
    step: Float,
    apuImage: ImageBitmap
) {
    for (food in foodPoints) {
        val row = food.y
        val col = food.x

        val top = (row * step).toInt()
        val bottom = ((row + 1) * step).toInt()
        val tileHeight = bottom - top

        val left = (col * step).toInt()
        val right = ((col + 1) * step).toInt()
        val tileWidth = right - left

        drawImage(
            image = apuImage,
            dstOffset = IntOffset(left, top),
            dstSize = IntSize(tileWidth, tileHeight)
        )
    }
}

fun ContentDrawScope.drawNet(
    brush: SolidColor,
    grassImage: ImageBitmap,
    mapSize: Int,
    step: Float
) {
    for (row in 0 until mapSize) {
        val top = (row * step).toInt()
        val bottom = ((row + 1) * step).toInt()
        val tileHeight = bottom - top

        for (col in 0 until mapSize) {
            val left = (col * step).toInt()
            val right = ((col + 1) * step).toInt()
            val tileWidth = right - left

            drawImage(
                image = grassImage,
                dstOffset = IntOffset(left, top),
                dstSize = IntSize(tileWidth, tileHeight)
            )
        }
    }

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
