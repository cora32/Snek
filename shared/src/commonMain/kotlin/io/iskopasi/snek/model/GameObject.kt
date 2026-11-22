package io.iskopasi.snek.model

val charMap = mapOf(
    0 to "cirno_sprite",
    1 to "daiyousei",
    2 to "cirno_sprite",
)

val charTypeMap = mapOf(
    0 to TileType.Cirno,
    1 to TileType.Daiyousei,
    2 to TileType.Cirno,
)

val charIndexMap = mapOf(
    TileType.Cirno to 0,
    TileType.Daiyousei to 1,
    TileType.Cirno to 2,
)


enum class Direction {
    Up,
    Down,
    Left,
    Right
}

enum class StepState {
    Left,
    Right,
    Idle
}

enum class ResName {
    Grass1,
    Grass2,
    Grass3,
    GrassWhiteFlower,
    GrassYellowFlower,
    Tree1,
    Tree2,
    Tree3,
}

data class Point(val x: Int, val y: Int)

data class Segment(
    val point: Point,
    var prevPoint: Point? = null
) {
    fun getDirection(): Direction {
        if (prevPoint == null) {
            return Direction.Right
        } else {
            val dx = point.x - prevPoint!!.x
            val dy = point.y - prevPoint!!.y

            return when {
                dx > 0 -> Direction.Right
                dx < 0 -> Direction.Left
                dy > 0 -> Direction.Down
                dy < 0 -> Direction.Up
                else -> Direction.Right
            }
        }
    }
}

class SnekData(val size: Int) {
    val body: ArrayDeque<Segment> = ArrayDeque<Segment>()

    init {
        initBody(size = size)
    }

    fun initBody(size: Int) {
        val center = (size / 2f).toInt()

        val segment1 = Segment(
            point = Point(center + 1, center),
            prevPoint = Point(center, center)
        )
        val segment2 = Segment(
            point = Point(center, center),
            prevPoint = Point(center - 1, center)
        )
        val segment3 = Segment(
            point = Point(center - 1, center),
            prevPoint = null
        )

        body.add(segment1)
        body.add(segment2)
        body.add(segment3)
    }

    private fun moveOrGrow(newX: Int, newY: Int, foodSet: MutableSet<Point>): Boolean {
        val isFood = isFood(newX, newY, foodSet)

        if (isFood) {
            addSegment(newX, newY)
            eatFood(newX, newY, foodSet)
        } else {
            move(newX, newY)
        }

        return isFood
    }

    fun move(x: Int, y: Int) {
        body.removeLast()

        val point = when {
            x < 0 -> Point(size - 1, y)
            x >= size -> Point(0, y)
            y < 0 -> Point(x, size - 1)
            y >= size -> Point(x, 0)
            else -> Point(x, y)
        }

        addSegment(newX = point.x, newY = point.y)

        println("move: $point")

        for (segment in body) {
            println(segment)
        }
    }

    fun moveRight(foodSet: MutableSet<Point>): Boolean {
        val head = body.first().point

        val newX = head.x + 1
        val newY = head.y

        val isFood = moveOrGrow(newX, newY, foodSet)

        return isFood
    }

    private fun eatFood(x: Int, y: Int, foodSet: MutableSet<Point>) {
        println("foodSet1: ${foodSet.size}")
        foodSet.remove(Point(x, y))
        println("foodSet2: ${foodSet.size}")
    }

    fun moveLeft(foodSet: MutableSet<Point>): Boolean {
        val head = body.first().point

        val newX = head.x - 1
        val newY = head.y

        return moveOrGrow(newX, newY, foodSet)
    }

    fun moveUp(foodSet: MutableSet<Point>): Boolean {
        val head = body.first().point

        val newX = head.x
        val newY = head.y - 1

        return moveOrGrow(newX, newY, foodSet)
    }

    fun moveDown(foodSet: MutableSet<Point>): Boolean {
        val head = body.first().point

        val newX = head.x
        val newY = head.y + 1

        return moveOrGrow(newX, newY, foodSet)
    }

    private fun isFood(newX: Int, newY: Int, foodSet: MutableSet<Point>): Boolean {
        return foodSet.contains(Point(newX, newY))
    }

    private fun addSegment(newX: Int, newY: Int) {
        val segment = Segment(
            point = Point(newX, newY),
            prevPoint = body.first().point
        )
        body.addFirst(segment)
    }

    fun getCharacter(x: Int, y: Int): TileType {
        body.forEachIndexed { index, segment ->
            if (segment.point.x == x && segment.point.y == y) {
                return charTypeMap[index] ?: TileType.None
            }
        }

        return TileType.None
    }

    fun getSegment(tileType: TileType): Segment? {
        val characterIndex = charIndexMap[tileType] ?: -1
        return body.getOrNull(characterIndex)
    }

    fun getCharacterResNames(stepState: StepState): Map<TileType, String> {
        val characterResNames = mutableMapOf<TileType, String>()

        println("==> body.size: ${body.size}")
        for (i in body.indices) {
            val segment = body[i]
            val tileType = charTypeMap[i] ?: TileType.None
            if (tileType != TileType.None) {
                val direction = segment.getDirection()
                val resName =
                    "${tileType.resName}_${direction.name.lowercase()}_${stepState.name.lowercase()}"

                characterResNames[tileType] = resName

                println("==> $resName")
            }
        }

        return characterResNames
    }
}

class GameObject(val size: Int) {
    val snekData: SnekData by lazy { getSnekData(size = size) }
    val tileSettings = TileSettings(mapSize = size, snekData = snekData)
    var score: Int = 0

    private fun getSnekData(size: Int): SnekData {
        val snekData = SnekData(size = size)

        return snekData
    }

    fun moveRight() {
        val isScore = snekData.moveRight(foodSet = tileSettings.apuPoints)

        if (isScore) {
            score++
        }
    }

    fun moveLeft() {
        val isScore = snekData.moveLeft(foodSet = tileSettings.apuPoints)

        if (isScore) {
            score++
        }
    }

    fun moveUp() {
        val isScore = snekData.moveUp(foodSet = tileSettings.apuPoints)

        if (isScore) {
            score++
        }
    }

    fun moveDown() {
        val isScore = snekData.moveDown(foodSet = tileSettings.apuPoints)

        if (isScore) {
            score++
        }
    }

    fun toMatrix(): Array<Array<List<TileType>>> {
        val matrix = Array(size) {
            Array(size) {
                emptyList<TileType>()
            }
        }

        for (x in 0..<size) {
            for (y in 0..<size) {
                val vector = mutableListOf<TileType>()

                val grassType = tileSettings.getGrassAt(x, y)
                val topItem = tileSettings.getTopItem(x, y)

                vector.add(grassType) // Add grass tile first
                if (topItem != TileType.None)
                    vector.add(topItem)

                matrix[x][y] = vector
            }
        }

        return matrix
    }

    fun getCharacterResNames(stepState: StepState): Map<TileType, String> =
        snekData.getCharacterResNames(stepState = stepState)
}