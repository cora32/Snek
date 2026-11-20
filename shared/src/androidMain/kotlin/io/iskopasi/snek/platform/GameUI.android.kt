package io.iskopasi.snek.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.snek.model.StepState
import io.iskopasi.snek.model.TileType
import io.iskopasi.snek.ui.loadResMap
import io.iskopasi.snek.viewmodel.SnekViewModel
import kotlin.math.min

@Composable
actual fun GameUI(
    modifier: Modifier,
    viewModel: SnekViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val matrix = state.matrix
    val resMap = loadResMap()
//    val chaResMap = rememberCharacterImageBitmapMap()

    Box(
        modifier = Modifier.drawWithCache {
            val netBrush = SolidColor(Color(0xffffffff))
            val step = min(size.width, size.height) / state.snekBody.size + 2
//            val step = min(size.width, size.height) / state.mapSize + 10

            onDrawWithContent {
                drawTiles(
                    matrix = matrix,
                    resMap = resMap,
                    step = step,
                    stepState = state.stepState
                )

            }
        },

        ) {

    }
}


private fun ContentDrawScope.drawTiles(
    matrix: Array<Array<List<TileType>>>,
    resMap: Map<TileType, ImageBitmap>,
    step: Float,
    stepState: StepState,
) {
    for (x in matrix.indices) {
        for (y in matrix.indices) {
            val tiles = matrix[x][y]

            for (tileType in tiles.asReversed()) {
//                val bitmap = if (tileType.isCharacter)
//                    loadCharacter(
//                        stepState = stepState,
//                    )
//                else
//                    resMap[tileType]
                val bitmap = resMap[tileType]

                val row = y
                val col = x

                val top = (row * step).toInt()
                val bottom = ((row + 1) * step).toInt()
                val tileHeight = bottom - top

                val left = (col * step).toInt()
                val right = ((col + 1) * step).toInt()
                val tileWidth = right - left

                bitmap?.let {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(left, top),
                        dstSize = IntSize(tileWidth, tileHeight)
                    )
                }
            }
        }

    }
}