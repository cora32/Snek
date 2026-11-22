package io.iskopasi.snek.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.snek.model.SnekData
import io.iskopasi.snek.model.StepState
import io.iskopasi.snek.model.TileType
import io.iskopasi.snek.ui.loadDynamicRes
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
    val characterResMap = loadDynamicRes(characterResNameMap = state.characterResNames)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val gridSide = min(size.width, size.height)
                val step = gridSide / 10
                val centerX = step * (state.mapSize / 2)
                val centerY = step * (state.mapSize / 2)
                val offsetX = (size.width - gridSide) / 2f - centerX
                val offsetY = (size.height - gridSide) / 2f - centerY

                onDrawWithContent {
                    drawTiles(
                        matrix = matrix,
                        resMap = resMap,
                        step = step,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        stepState = state.stepState,
                        characterResMap = characterResMap,
                        snekData = viewModel.gameObject.snekData
                    )
                }
            },
    )
}


private fun ContentDrawScope.drawTiles(
    matrix: Array<Array<List<TileType>>>,
    resMap: Map<TileType, ImageBitmap>,
    step: Float,
    offsetX: Float,
    offsetY: Float,
    stepState: StepState,
    characterResMap: Map<TileType, ImageBitmap>,
    snekData: SnekData,
) {
    for (x in matrix.indices) {
        for (y in matrix.indices) {
            val tiles = matrix[x][y]

            for (tileType in tiles) {
                drawTile(
                    tileType = tileType,
                    resMap = resMap,
                    characterResMap = characterResMap,
                    snekData = snekData,
                    x = x,
                    y = y,
                    step = step,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    stepState = stepState,
                )
            }
        }
    }
}

private fun ContentDrawScope.drawTile(
    tileType: TileType,
    resMap: Map<TileType, ImageBitmap>,
    characterResMap: Map<TileType, ImageBitmap>,
    snekData: SnekData,
    x: Int,
    y: Int,
    step: Float,
    offsetX: Float,
    offsetY: Float,
    stepState: StepState
) {
    val bitmap = if (tileType.isCharacter) {
        characterResMap[tileType]
    } else
        resMap[tileType]


    val row = y
    val col = x

    val top = (offsetY + row * step).toInt()
    val bottom = (offsetY + (row + 1) * step).toInt()
    val tileHeight = bottom - top

    val left = (offsetX + col * step).toInt()
    val right = (offsetX + (col + 1) * step).toInt()
    val tileWidth = right - left

    bitmap?.let {
        drawImage(
            image = bitmap,
            dstOffset = IntOffset(left, top),
            dstSize = IntSize(tileWidth, tileHeight)
        )
    }
}