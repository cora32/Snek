package io.iskopasi.snek.model

import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import kotlin.getValue


data class Point(val x: Int, val y: Int)

class SnekData(val size: Int) {
    val body: ArrayDeque<Point> = ArrayDeque<Point>()

    init {
        initBody(size = size)
    }

    fun initBody(size: Int) {
        val center = (size / 2f).toInt()

        body.add(Point(center + 1, center))
        body.add(Point(center, center))
        body.add(Point(center - 1, center))
    }

    private fun moveOrGrow(newX: Int, newY: Int, foodMap: MutableMap<Point, Point>): Boolean {
        val isScore = isFood(newX, newY, foodMap)

        if (isScore) {
            addSegment(newX, newY)
        } else {
            move(newX, newY)
        }

        return isScore
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

        body.addFirst(point)
    }

    fun moveRight(foodMap: MutableMap<Point, Point>): Boolean {
        val head = body.first()

        val newX = head.x + 1
        val newY = head.y

        val isScore = moveOrGrow(newX, newY, foodMap)

        return isScore
    }

    fun moveLeft(foodMap: MutableMap<Point, Point>): Boolean {
        val head = body.first()

        val newX = head.x - 1
        val newY = head.y

        return moveOrGrow(newX, newY, foodMap)
    }

    fun moveUp(foodMap: MutableMap<Point, Point>): Boolean {
        val head = body.first()

        val newX = head.x
        val newY = head.y - 1

        return moveOrGrow(newX, newY, foodMap)
    }

    fun moveDown(foodMap: MutableMap<Point, Point>): Boolean {
        val head = body.first()

        val newX = head.x
        val newY = head.y + 1

        return moveOrGrow(newX, newY, foodMap)
    }

    private fun isFood(newX: Int, newY: Int, foodMap: MutableMap<Point, Point>): Boolean {
        return foodMap.containsKey(Point(newX, newY))
    }

    private fun addSegment(newX: Int, newY: Int) {
        body.addFirst(Point(newX, newY))
    }
}

class GameObject(val size: Int) {
    val foodMap: MutableMap<Point, Point> by lazy { getFoodData(size = size) }
    val snekData: SnekData by lazy { getSnekData(size = size) }
    var score: Int = 0

    private fun getSnekData(size: Int): SnekData {
        val snekData = SnekData(size = size)

        return snekData
    }

    private fun getFoodData(size: Int): MutableMap<Point, Point> {
        val foodMap = mutableMapOf<Point, Point>()

        repeat(20) {
            val point = Point((0..<size).random(), (0..<size).random())
            foodMap[point] = point
        }

        return foodMap
    }

    fun moveRight() {
        val isScore = snekData.moveRight(foodMap = foodMap)

        if (isScore) {
            score++
        }
    }

    fun moveLeft() {
        val isScore = snekData.moveLeft(foodMap = foodMap)

        if (isScore) {
            score++
        }
    }

    fun moveUp() {
        val isScore = snekData.moveUp(foodMap = foodMap)

        if (isScore) {
            score++
        }
    }

    fun moveDown() {
        val isScore = snekData.moveDown(foodMap = foodMap)

        if (isScore) {
            score++
        }
    }
}