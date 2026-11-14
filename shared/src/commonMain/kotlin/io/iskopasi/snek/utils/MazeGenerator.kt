package io.iskopasi.snek.utils

// Core Constants
const val WALL = 0
const val PATH = 1

/**
 * Generates a perfect labyrinth on a grid map.
 * To house both paths and walls dynamically, the array scale maps to (2*size + 1).
 */
fun generateLabyrinth(size: Int): Array<IntArray> {
    val gridSize = 2 * size + 1
    // Initialize the entire map as solid walls (0)
    val labyrinthMap = Array(gridSize) { IntArray(gridSize) { WALL } }

    // Track visited status of the logical rooms
    val visited = Array(size) { BooleanArray(size) { false } }

    // KMP ArrayDeque stack to handle backtracking without risking StackOverflowError
    val stack = ArrayDeque<Pair<Int, Int>>()

    // Start at the top-left logical room (0, 0) -> maps to (1, 1) in the physical grid
    stack.addLast(Pair(0, 0))
    visited[0][0] = true
    labyrinthMap[1][1] = PATH

    // Define the 4 cardinal directions (dx, dy)
    val directions = listOf(
        Pair(0, 1),  // East
        Pair(1, 0),  // South
        Pair(0, -1), // West
        Pair(-1, 0)  // North
    )

    while (stack.isNotEmpty()) {
        val (cx, cy) = stack.last()

        // Randomly shuffle directions for an unpredictable maze pattern
        val shuffledDirections = directions.shuffled()
        var carved = false

        for ((dx, dy) in shuffledDirections) {
            val nx = cx + dx
            val ny = cy + dy

            // Check if the neighboring logical room is valid and unvisited
            if (nx in 0 until size && ny in 0 until size && !visited[nx][ny]) {
                // Mark as visited
                visited[nx][ny] = true

                // Convert logical room coordinates to physical grid positions
                val pxWall = 1 + cx + nx
                val pyWall = 1 + cy + ny
                val pxNext = 1 + 2 * nx
                val pyNext = 1 + 2 * ny

                // Carve the path through the wall to the next room
                labyrinthMap[pxWall][pyWall] = PATH
                labyrinthMap[pxNext][pyNext] = PATH

                // Move deeper into the grid
                stack.addLast(Pair(nx, ny))
                carved = true
                break
            }
        }

        if (!carved) {
            stack.removeLast() // Backtrack if trapped at a dead end
        }
    }

    return labyrinthMap
}

/**
 * Safely fetches the map layout at any coordinate pair (x, y)
 */
fun getCoordinateType(grid: Array<IntArray>, x: Int, y: Int): String {
    return if (x in grid.indices && y in grid[0].indices) {
        if (grid[x][y] == PATH) "PATH" else "WALL"
    } else {
        "OUT_OF_BOUNDS"
    }
}
