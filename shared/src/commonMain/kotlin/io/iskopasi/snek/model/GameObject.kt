package io.iskopasi.snek.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

val charMap = mapOf(
    0 to "cirno_sprite",
    1 to "cirno_sprite",
    2 to "cirno_sprite",
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

class StepAnimator {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var _stepState = MutableStateFlow<StepState>(StepState.Idle)
    val stepState = _stepState.asStateFlow()

    var delayedTask: Job? = null

    private val timeSource = TimeSource.Monotonic
    private var lastStepTime = timeSource.markNow()

    suspend fun updateStepState() {
        val elapsedMs = lastStepTime.elapsedNow().inWholeMilliseconds
        if (elapsedMs > 200) { // Switch legs every 200ms
            switchLegs()
            lastStepTime = timeSource.markNow()
        }

        // Set to Idle after 0.5 second
        delayedTask?.cancel()
        delayedTask = scope.launch {
            delay(500L)
            _stepState.update {
                StepState.Idle
            }
        }
    }

    private fun switchLegs() {
        _stepState.update {
            when (it) {
                StepState.Idle -> StepState.Right
                StepState.Right -> StepState.Left
                StepState.Left -> StepState.Right
            }
        }

    }
}

class SnekResourceLoader {
    fun calculateImageNameMap(
        segments: List<Segment>,
        stepState: StepState,
    ): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        for (i in segments.indices) {
            val segment = segments[i]

            val characterName = charMap[i]
            val direction = segment.getDirection()

            result[i] =
                "${characterName}_${direction.name.lowercase()}_${stepState.name.lowercase()}"
        }

        return result
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
}

class GameObject(val size: Int) {
    val foodSet: MutableSet<Point> by lazy { getFoodData(size = size) }
    val snekData: SnekData by lazy { getSnekData(size = size) }
    var score: Int = 0

    private fun getSnekData(size: Int): SnekData {
        val snekData = SnekData(size = size)

        return snekData
    }

    private fun getFoodData(size: Int): MutableSet<Point> {
        val foodSet = mutableSetOf<Point>()

        repeat(20) {
            val point = Point((0..<size).random(), (0..<size).random())
            foodSet.add(point)
        }

        return foodSet
    }

    fun moveRight() {
        val isScore = snekData.moveRight(foodSet = foodSet)

        if (isScore) {
            score++
        }
    }

    fun moveLeft() {
        val isScore = snekData.moveLeft(foodSet = foodSet)

        if (isScore) {
            score++
        }
    }

    fun moveUp() {
        val isScore = snekData.moveUp(foodSet = foodSet)

        if (isScore) {
            score++
        }
    }

    fun moveDown() {
        val isScore = snekData.moveDown(foodSet = foodSet)

        if (isScore) {
            score++
        }
    }
}