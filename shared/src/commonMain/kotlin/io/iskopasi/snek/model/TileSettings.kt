package io.iskopasi.snek.model

import io.iskopasi.snek.platform.moreRandom


class TileSettings(
    val mapSize: Int,
) {
    var yellowFlowerIndices: Set<Int> = emptySet()
    var whiteFlowerIndices: Set<Int> = emptySet()
    var apuPoints: MutableSet<Point> = mutableSetOf()
    var grassTile1Index: Int = 1
    var grassTile2Index: Int = 1

    init {
        init()
    }

    fun initYellowFlowerNumbers() {
        val set = mutableSetOf<Int>()
        val count = (0..((mapSize * mapSize) / 6)).moreRandom()
        val range = 0..<mapSize * mapSize

        repeat(count) {
            set.add(range.moreRandom())
        }

        yellowFlowerIndices = set.toSet()
    }

    fun initWhiteFlowerNumbers() {
        val set = mutableSetOf<Int>()
        val count = (0..((mapSize * mapSize) / 8)).moreRandom()
        val range = 0..<mapSize * mapSize

        repeat(count) {
            set.add(range.moreRandom())
        }

        whiteFlowerIndices = set.toSet()
    }

    fun initApu() {
        val set = mutableSetOf<Point>()
        val count = (0..((mapSize * mapSize) / 15)).moreRandom()

        repeat(count) {
            val point = Point((0..<mapSize).random(), (0..<mapSize).random())
            set.add(point)
        }

        apuPoints = set
    }

    fun init() {
        initYellowFlowerNumbers()
        initWhiteFlowerNumbers()
        initApu()

        grassTile1Index = (2..mapSize).moreRandom()
        grassTile2Index = (5..mapSize).moreRandom()
    }
}