package io.iskopasi.snek.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.snek.model.TileType
import io.iskopasi.snek.ui.loadDynamicRes
import io.iskopasi.snek.ui.loadResMap
import io.iskopasi.snek.viewmodel.SnekViewModel

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
            .background(Color(0xff241401))
            .drawWithCache {
                val step = 100
//                val step = min(size.width, size.height) / max(displayRows, displayColumns)
                val displayRows = (size.height / step + 1).toInt()
                val displayColumns = (size.width / step + 1).toInt()
                val screenCenter = size.center

                onDrawWithContent {
                    drawTiles(
                        matrix = matrix,
                        resMap = resMap,
                        characterResMap = characterResMap,
                        step = step,
                        displayRows = displayRows,
                        displayColumns = displayColumns,
                        screenCenter = screenCenter,
                    )
                }
            },
    )
}

fun ContentDrawScope.drawTiles(
    matrix: Array<Array<List<TileType>>>,
    resMap: Map<TileType, ImageBitmap>,
    characterResMap: Map<TileType, ImageBitmap>,
    step: Int,
    displayRows: Int,
    displayColumns: Int,
    screenCenter: Offset
) {
    val viewportOffsetX = (step * displayColumns) / 2
    val viewportOffsetY = (step * displayRows) / 2

    for (displayX in 0..<displayColumns) {
        val left = (step * displayX)
        val right = (step * (displayX + 1))
        val tileWidth = right - left

        for (displayY in 0..<displayRows) {
            val top = (step * displayY)
            val bottom = (step * (displayY + 1))
            val tileHeight = bottom - top

            val tileVector =
                getTileCoordinates(displayX, displayY, displayRows, displayColumns, matrix)

            for (tileType in tileVector) {
                val bitmap = if (tileType.isCharacter) {
                    characterResMap[tileType]
                } else
                    resMap[tileType]

                bitmap?.let {
                    val fixedX = left + screenCenter.x.toInt() - viewportOffsetX
                    val fixedY = top + screenCenter.y.toInt() - viewportOffsetY

                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(
                            fixedX,
                            fixedY,
                        ),
                        dstSize = IntSize(tileWidth, tileHeight)
                    )
                }
            }
        }
    }
}

fun getTileCoordinates(
    displayX: Int,
    displayY: Int,
    displayRows: Int,
    displayColumns: Int,
    matrix: Array<Array<List<TileType>>>,
): List<TileType> {
    val matrixCenter = matrix.size / 2
    val x = matrixCenter - displayColumns / 2 + displayX
    val y = matrixCenter - displayRows / 2 + displayY

    return matrix[x][y]
}