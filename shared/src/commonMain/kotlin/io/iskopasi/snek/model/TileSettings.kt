package io.iskopasi.snek.model

import io.iskopasi.snek.platform.moreRandom


class TileSettings(
    val mapSize: Int,
) {
    var yellowFlowerNumbers: Set<Int> = emptySet()
    var whiteFlowerNumbers: Set<Int> = emptySet()
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

        yellowFlowerNumbers = set.toSet()
    }

    fun initWhiteFlowerNumbers() {
        val set = mutableSetOf<Int>()
        val count = (0..((mapSize * mapSize) / 8)).moreRandom()
        val range = 0..<mapSize * mapSize

        repeat(count) {
            set.add(range.moreRandom())
        }

        whiteFlowerNumbers = set.toSet()
    }

    fun init() {
        initYellowFlowerNumbers()
        initWhiteFlowerNumbers()

        grassTile1Index = (2..mapSize).moreRandom()
        grassTile2Index = (5..mapSize).moreRandom()
    }
}