package io.iskopasi.snek.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.iskopasi.snek.model.Point
import io.iskopasi.snek.model.ResName
import io.iskopasi.snek.model.Segment
import io.iskopasi.snek.model.TileSettings
import io.iskopasi.snek.model.TreeType
import io.iskopasi.snek.utils.WALL
import io.iskopasi.snek.utils.loadBitmapByName
import org.jetbrains.compose.resources.imageResource
import snek.shared.generated.resources.Res
import snek.shared.generated.resources.grass_01
import snek.shared.generated.resources.grass_02
import snek.shared.generated.resources.grass_03
import snek.shared.generated.resources.grass_white_flower
import snek.shared.generated.resources.grass_yellow_flower
import snek.shared.generated.resources.tree_01_green
import snek.shared.generated.resources.tree_02_sakura
import snek.shared.generated.resources.tree_03_snow_pine
import kotlin.math.min


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
fun rememberCharacterImageBitmapMap(imageNameMap: Map<Int, String>): Map<Int, ImageBitmap> {
    val bitmapMapState = produceState(
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
fun rememberGeneralBitmapMap(modifier: Modifier = Modifier): Map<ResName, ImageBitmap> {
    val g1 = imageResource(Res.drawable.grass_01)
    val g2 = imageResource(Res.drawable.grass_02)
    val g3 = imageResource(Res.drawable.grass_03)
    val gwf = imageResource(Res.drawable.grass_white_flower)
    val gyf = imageResource(Res.drawable.grass_yellow_flower)
    val tree1 = imageResource(Res.drawable.tree_01_green)
    val tree2 = imageResource(Res.drawable.tree_02_sakura)
    val tree3 = imageResource(Res.drawable.tree_03_snow_pine)

    val state = produceState(
        initialValue = emptyMap(),
        keys = arrayOf(Unit)
    ) {
        val map = mutableMapOf<ResName, ImageBitmap>()

        map[ResName.Grass1] = g1
        map[ResName.Grass2] = g2
        map[ResName.Grass3] = g3
        map[ResName.GrassWhiteFlower] = gwf
        map[ResName.GrassYellowFlower] = gyf
        map[ResName.Tree1] = tree1
        map[ResName.Tree2] = tree2
        map[ResName.Tree3] = tree3

        value = map
    }

    return state.value
}

@Composable
fun Modifier.drawUI(
    snekBody: List<Segment>,
    foodPoints: Set<Point>,
    mapSize: Int,
    generalBitmapMap: Map<ResName, ImageBitmap>,
    apuImage: ImageBitmap,
    imageBitmapMap: Map<Int, ImageBitmap>,
    tileSettings: TileSettings
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
                generalBitmapMap = generalBitmapMap,
                mapSize = currentMapSize,
                step = step,
                tileSettings = tileSettings
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
    generalBitmapMap: Map<ResName, ImageBitmap>,
    tileSettings: TileSettings,
    mapSize: Int,
    step: Float
) {
    var tileIndex = 0
    val treeRes = when (tileSettings.treeType) {
        TreeType.Tree1 -> ResName.Tree1
        TreeType.Tree2 -> ResName.Tree2
        TreeType.Tree3 -> ResName.Tree3
    }

    for (row in 0 until mapSize) {
        val top = (row * step).toInt()
        val bottom = ((row + 1) * step).toInt()
        val tileHeight = bottom - top

        for (col in 0 until mapSize) {
            val left = (col * step).toInt()
            val right = ((col + 1) * step).toInt()
            val tileWidth = right - left
            tileIndex++

            val grassImage = when {
                tileSettings.yellowFlowerIndices.contains(tileIndex) -> generalBitmapMap[ResName.GrassYellowFlower]
                tileSettings.whiteFlowerIndices.contains(tileIndex) -> generalBitmapMap[ResName.GrassWhiteFlower]
                else -> when {
                    (tileIndex) % tileSettings.grassTile1Index == 0 -> generalBitmapMap[ResName.Grass1]
                    (tileIndex) % tileSettings.grassTile2Index == 0 -> generalBitmapMap[ResName.Grass2]
                    else -> generalBitmapMap[ResName.Grass3]
                }
            }

            // Floor
            grassImage?.let {
                drawImage(
                    image = it,
                    dstOffset = IntOffset(left, top),
                    dstSize = IntSize(tileWidth, tileHeight)
                )
            }

            // Wall
            if (tileSettings.treeMaze[col][row] == WALL) {
                val wallResource = generalBitmapMap[treeRes]

                wallResource?.let {
                    drawImage(
                        image = it,
                        dstOffset = IntOffset(left, top),
                        dstSize = IntSize(tileWidth, tileHeight)
                    )
                }
            }

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
