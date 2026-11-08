package io.iskopasi.snek.model

import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import kotlin.getValue


data class Point(val x: Int, val y: Int)

class SnekData(val mapSize: IntSize) {
    val body: ArrayDeque<Point> = ArrayDeque<Point>()

    init {
        initBody(mapSize = mapSize)
    }

    fun initBody(mapSize: IntSize) {
        val center = mapSize.center

        body.add(Point(center.x + 1, center.y))
        body.add(Point(center.x, center.y))
        body.add(Point(center.x - 1, center.y))
    }

    private fun moveOrGrow(newX: Int, newY: Int, foodMap: MutableMap<Point, Point>) {
        if (isFood(newX, newY, foodMap)) {
            addSegment(newX, newY)
        } else {
            move(newX, newY)
        }
    }

    fun move(x: Int, y: Int) {
        body.removeLast()

        val point = when {
            x < 0 -> Point(mapSize.width - 1, y)
            x >= mapSize.width -> Point(0, y)
            y < 0 -> Point(x, mapSize.height - 1)
            y >= mapSize.height -> Point(x, 0)
            else -> Point(x, y)
        }

        body.addFirst(point)
    }

    fun moveRight(foodMap: MutableMap<Point, Point>) {
        val head = body.first()

        val newX = head.x + 1
        val newY = head.y

        moveOrGrow(newX, newY, foodMap)
    }

    fun moveLeft(foodMap: MutableMap<Point, Point>) {
        val head = body.first()

        val newX = head.x - 1
        val newY = head.y

        moveOrGrow(newX, newY, foodMap)
    }

    fun moveUp(foodMap: MutableMap<Point, Point>) {
        val head = body.first()

        val newX = head.x
        val newY = head.y - 1

        moveOrGrow(newX, newY, foodMap)
    }

    fun moveDown(foodMap: MutableMap<Point, Point>) {
        val head = body.first()

        val newX = head.x
        val newY = head.y + 1

        moveOrGrow(newX, newY, foodMap)
    }

    private fun isFood(newX: Int, newY: Int, foodMap: MutableMap<Point, Point>): Boolean {
        return foodMap.containsKey(Point(newX, newY))
    }

    private fun addSegment(newX: Int, newY: Int) {
        body.addFirst(Point(newX, newY))
    }
}

class GameObject(val width: Int, val height: Int) {
    private val mapSize: IntSize by lazy { getMapSize(width, height) }
    private val snekData: SnekData by lazy { getSnekData(mapSize) }
    private val foodMap: MutableMap<Point, Point> by lazy { getFoodData() }

    fun getMapSize(width: Int, height: Int): IntSize = IntSize(width, height)

    private fun getSnekData(mapSize: IntSize): SnekData {
        val snekData = SnekData(mapSize = mapSize)

        return snekData
    }

    private fun getFoodData(): MutableMap<Point, Point> {
        val foodMap = mutableMapOf<Point, Point>()

        repeat(20) {
            val point = Point((0..mapSize.width).random(), (0..mapSize.height).random())
            foodMap[point] = point
        }

        return foodMap
    }


    fun moveRight() {
        snekData.moveRight(foodMap = foodMap)
    }

    fun moveLeft() {
        snekData.moveLeft(foodMap = foodMap)
    }

    fun moveUp() {
        snekData.moveUp(foodMap = foodMap)
    }

    fun moveDown() {
        snekData.moveDown(foodMap = foodMap)
    }
}