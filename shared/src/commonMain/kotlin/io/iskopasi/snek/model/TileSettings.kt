package io.iskopasi.snek.model

import io.iskopasi.snek.platform.moreRandom
import io.iskopasi.snek.utils.generateLabyrinth

enum class TreeType {
    Tree1,
    Tree2,
    Tree3
}

class TileSettings(
    val mapSize: Int,
) {
    val yellowFlowerIndices: Set<Int> by lazy { getYellowFlowerNumbers() }
    val whiteFlowerIndices: Set<Int> by lazy { getWhiteFlowerNumbers() }
    val apuPoints: MutableSet<Point> by lazy { getApu() }
    val grassTile1Index: Int = (2..mapSize).moreRandom()
    val grassTile2Index: Int = (5..mapSize).moreRandom()
    val treeMaze: Array<IntArray> by lazy { generateLabyrinth(mapSize) }
    val treeType: TreeType = TreeType.entries.toTypedArray().random()

    fun getYellowFlowerNumbers(): Set<Int> {
        val set = mutableSetOf<Int>()
        val count = (0..((mapSize * mapSize) / 6)).moreRandom()
        val range = 0..<mapSize * mapSize

        repeat(count) {
            set.add(range.moreRandom())
        }

        return set.toSet()
    }

    fun getWhiteFlowerNumbers(): Set<Int> {
        val set = mutableSetOf<Int>()
        val count = (0..((mapSize * mapSize) / 8)).moreRandom()
        val range = 0..<mapSize * mapSize

        repeat(count) {
            set.add(range.moreRandom())
        }

        return set.toSet()
    }

    fun getApu(): MutableSet<Point> {
        val set = mutableSetOf<Point>()
        val count = (0..((mapSize * mapSize) / 15)).moreRandom()

        repeat(count) {
            val point = Point((0..<mapSize).random(), (0..<mapSize).random())
            set.add(point)
        }

        return set
    }
}