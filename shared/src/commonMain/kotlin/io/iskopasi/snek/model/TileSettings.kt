package io.iskopasi.snek.model

import io.iskopasi.snek.platform.moreRandom
import io.iskopasi.snek.utils.WALL
import io.iskopasi.snek.utils.generateLabyrinth

enum class TreeType {
    Tree1,
    Tree2,
    Tree3
}

enum class GrassType {
    Grass1,
    Grass2,
    Grass3,
    GrassYellowFlower,
    GrassWhiteFlower,
}

enum class TileType(val resName: String = "", val isCharacter: Boolean = false) {

    Tree1,
    Tree2,
    Tree3,
    Grass1,
    Grass2,
    Grass3,
    GrassWhiteFlower,
    GrassYellowFlower,
    Apu,
    Cirno("cirno_sprite", true),
    Daiyousei("daiyousei", true),
    None;
}

class TileSettings(
    val mapSize: Int,
    val snekData: SnekData
) {
    val yellowFlowerIndices: Set<Int> by lazy { getYellowFlowerNumbers() }
    val whiteFlowerIndices: Set<Int> by lazy { getWhiteFlowerNumbers() }
    val apuPoints: MutableSet<Point> by lazy { getApu() }
    val grassTile1Index: Int = (2..mapSize).moreRandom()
    val grassTile2Index: Int = (5..mapSize).moreRandom()
    val treeMaze: Array<IntArray> by lazy { generateLabyrinth(mapSize) }
    val treeType: TreeType = TreeType.entries.toTypedArray().random()
    val treeTypeNew: TileType = listOf(TileType.Tree1, TileType.Tree2, TileType.Tree3).random()
    val grassMatrix: Array<Array<TileType>> = Array(mapSize) {
        Array(mapSize) {
            TileType.Grass1
        }
    }
    val foodMatrix: Array<Array<TileType>> = Array(mapSize) {
        Array(mapSize) {
            TileType.Apu
        }
    }

    init {
        initGrassMatrix()
        initFoodMatrix()
    }

    private fun initGrassMatrix() {
        val grassArray = listOf<TileType>(TileType.Grass1, TileType.Grass2, TileType.Grass3)

        for (x in (0..<mapSize)) {
            for (y in (0..<mapSize)) {
                val rand = (0..100).moreRandom()

                val item = when {
                    rand > 90 -> TileType.GrassWhiteFlower
                    rand > 80 -> TileType.GrassYellowFlower
                    else -> grassArray.random()
                }

                grassMatrix[x][y] = item
            }
        }
    }

    private fun initFoodMatrix() {
        for (x in (0..<mapSize)) {
            for (y in (0..<mapSize)) {
                val rand = (0..100).moreRandom()

                val item = when {
                    rand > 90 -> TileType.Apu
                    else -> TileType.None
                }

                foodMatrix[x][y] = item
            }
        }
    }

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

    fun getGrassAt(x: Int, y: Int): TileType {
        return grassMatrix[x][y]
    }

    fun getTopItem(x: Int, y: Int): TileType {
        val isWall = treeMaze[x][y] == WALL
        val isFood = foodMatrix[x][y] != TileType.None
        val character = snekData.getCharacter(x, y)
        val isCharacter = character != TileType.None

        return when {
            isWall -> treeTypeNew
            isFood -> foodMatrix[x][y]
            isCharacter -> character
            else -> TileType.None
        }
    }
}