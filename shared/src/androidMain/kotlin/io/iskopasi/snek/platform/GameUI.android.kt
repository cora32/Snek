package io.iskopasi.snek.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.snek.model.TileType
import io.iskopasi.snek.ui.loadDynamicRes
import io.iskopasi.snek.ui.loadResMap
import io.iskopasi.snek.viewmodel.SnekViewModel
import org.jetbrains.compose.resources.stringResource
import snek.shared.generated.resources.Res
import snek.shared.generated.resources.score

@Composable
actual fun GameUI(
    modifier: Modifier,
    viewModel: SnekViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val resMap = loadResMap()
    val characterResMap = loadDynamicRes(characterResNameMap = state.characterResNames)

    Box {
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
                            resMap = resMap,
                            characterResMap = characterResMap,
                            step = step,
                            displayRows = displayRows,
                            displayColumns = displayColumns,
                            screenCenter = screenCenter,
                            getTileVector = viewModel::getTileVector
                        )
                    }
                },
        )

        ControlsUI(
            onLeft = viewModel::moveLeft,
            onRight = viewModel::moveRight,
            onUp = viewModel::moveUp,
            onDown = viewModel::moveDown,
            onRestart = viewModel::onRestart,
            score = state.score
        )
    }
}

@Composable
fun ControlsUI(
    modifier: Modifier = Modifier,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onRestart: () -> Unit,
    score: Int
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left
        Column(
            modifier = Modifier.fillMaxHeight().background(Color.Black.copy(alpha = 0.3f))
                .padding(32.dp).padding(start = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onUp,

                ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Up",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onLeft
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Left",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onDown
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Down",
                    tint = Color.White
                )
            }
        }

        // Center
        Column(
            modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            Row(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(32.dp)
                ),
                verticalAlignment = Alignment.CenterVertically

            ) {
                IconButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = onRestart
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    stringResource(Res.string.score, score),
                    fontSize = 19.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Right
        Column(
            modifier = Modifier.fillMaxHeight().background(Color.Black.copy(alpha = 0.3f))
                .padding(32.dp).padding(end = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onUp
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Up",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onRight
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Right",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.padding(24.dp),
                onClick = onDown
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Down",
                    tint = Color.White
                )
            }
        }
    }
}

private fun ContentDrawScope.drawTiles(
    resMap: Map<TileType, ImageBitmap>,
    characterResMap: Map<TileType, ImageBitmap>,
    step: Int,
    displayRows: Int,
    displayColumns: Int,
    screenCenter: Offset,
    getTileVector: (Int, Int, Int, Int) -> List<TileType>
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
                getTileVector(
                    displayX,
                    displayY,
                    displayRows,
                    displayColumns,
                )

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