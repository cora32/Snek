package io.iskopasi.snek

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iskopasi.snek.model.Point
import io.iskopasi.snek.viewmodel.SnekViewModel
import kotlin.math.min

@Composable
@Preview
fun App(viewModel: SnekViewModel = viewModel { SnekViewModel(mapSize = 10) }) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Controls()
    }
}

@Composable
fun Controls(modifier: Modifier = Modifier) {

}

fun Modifier.drawUI(
    snekBody: ArrayDeque<Point>,
    foodPoints: Set<Point>,
    mapSize: Int
): Modifier {
    val modifier = Modifier.drawWithCache {
        val step = min(size.height, size.width) / mapSize
        val netBrush = SolidColor(Color(0xffffffff))

        val snekBrush = SolidColor(Color(0xff33ff33))

        val foodBrush = SolidColor(Color(0xffff2222))

        onDrawWithContent {
            drawRect(color = Color(0xFF121212))

            drawNet(brush = netBrush, mapSize = mapSize, step = step)

            drawFood(
                brush = foodBrush, foodPoints = foodPoints,
                step = step
            )

            drawSnek(
                brush = snekBrush, snekBody = snekBody,
                step = step
            )

            // Drawn each redraw phase?
            drawLine(
                brush = netBrush,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 10f
            )
        }
    }

    return this then modifier
}

fun ContentDrawScope.drawSnek(
    brush: SolidColor,
    snekBody: ArrayDeque<Point>,
    step: Float,
) {
    val radius = step / 3
    val offset = step / 2f

    for (segment in snekBody) {
        drawCircle(
            brush = brush,
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
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )

        // vertical line
        drawLine(
            brush = brush,
            start = Offset(i * step, 0f),
            end = Offset(i * step, size.height),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}