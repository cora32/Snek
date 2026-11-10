package io.iskopasi.snek.model


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

        body.addFirst(point)

        println("move: $point")

        for (segment in body) {
            println(segment)
        }
    }

    fun moveRight(foodSet: MutableSet<Point>): Boolean {
        val head = body.first()

        val newX = head.x + 1
        val newY = head.y

        val isFood = moveOrGrow(newX, newY, foodSet)

        return isFood
    }

    private fun eatFood(x: Int, y: Int, foodSet: MutableSet<Point>) {
        println("food1: ${foodSet.size}")
        foodSet.remove(Point(x, y))
        println("food2: ${foodSet.size}")
    }

    fun moveLeft(foodSet: MutableSet<Point>): Boolean {
        val head = body.first()

        val newX = head.x - 1
        val newY = head.y

        return moveOrGrow(newX, newY, foodSet)
    }

    fun moveUp(foodSet: MutableSet<Point>): Boolean {
        val head = body.first()

        val newX = head.x
        val newY = head.y - 1

        return moveOrGrow(newX, newY, foodSet)
    }

    fun moveDown(foodSet: MutableSet<Point>): Boolean {
        val head = body.first()

        val newX = head.x
        val newY = head.y + 1

        return moveOrGrow(newX, newY, foodSet)
    }

    private fun isFood(newX: Int, newY: Int, foodSet: MutableSet<Point>): Boolean {
        return foodSet.contains(Point(newX, newY))
    }

    private fun addSegment(newX: Int, newY: Int) {
        body.addFirst(Point(newX, newY))
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