package io.iskopasi.snek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.snek.model.GameObject
import io.iskopasi.snek.model.Point
import io.iskopasi.snek.model.Segment
import io.iskopasi.snek.model.SnekResourceLoader
import io.iskopasi.snek.model.StepAnimator
import io.iskopasi.snek.model.StepState
import io.iskopasi.snek.model.TileSettings
import io.iskopasi.snek.model.TileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val initialMapSize = 30

data class UIState(
    val score: Int,
    val snekBody: List<Segment>,
    val foodPoints: Set<Point>,
    val mapSize: Int = initialMapSize,
    val stepState: StepState,
    val imageNameMap: Map<Int, String>,
    val tileSettings: TileSettings,
    val matrix: Array<Array<List<TileType>>>,
    val characterResNames: Map<TileType, String>,
)


class SnekViewModel(val mapSize: Int = initialMapSize) : ViewModel() {
    private val resourceLoader = SnekResourceLoader()
    var gameObject = GameObject(size = mapSize)

    private val _state = MutableStateFlow(
        UIState(
            score = 0,
            snekBody = gameObject.snekData.body.toList(),
            foodPoints = gameObject.tileSettings.apuPoints.toSet(),
            mapSize = mapSize,
            stepState = StepState.Idle,
            imageNameMap = resourceLoader.calculateImageNameMap(
                gameObject.snekData.body.toList(),
                StepState.Idle
            ),
            tileSettings = gameObject.tileSettings,
            matrix = gameObject.toMatrix(),
            characterResNames = mapOf()
        )
    )
    private val stepAnimator = StepAnimator()
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            stepAnimator.stepState.collect {
                update()
            }
        }
    }

    fun moveUp() {
        viewModelScope.launch {
            gameObject.moveUp()
            stepAnimator.updateStepState()
            update()
        }
    }

    fun moveDown() {
        viewModelScope.launch {
            gameObject.moveDown()
            stepAnimator.updateStepState()
            update()
        }
    }

    fun moveLeft() {
        viewModelScope.launch {
            gameObject.moveLeft()
            stepAnimator.updateStepState()
            update()
        }
    }

    fun moveRight() {
        viewModelScope.launch {
            gameObject.moveRight()
            stepAnimator.updateStepState()
            update()
        }
    }

    fun onRestart() {
        viewModelScope.launch {
            gameObject = GameObject(size = mapSize)
            update()
        }
    }

    private fun update() {
        val currentStepState = stepAnimator.stepState.value
        val imageNameMap = resourceLoader.calculateImageNameMap(
            gameObject.snekData.body.toList(),
            currentStepState
        )
        val matrix = gameObject.toMatrix()
        val characterResNames = gameObject.getCharacterResNames(stepState = currentStepState)

        _state.update {
            it.copy(
                score = gameObject.score,
                snekBody = gameObject.snekData.body.toList(),
                foodPoints = gameObject.tileSettings.apuPoints.toSet(),
                stepState = currentStepState,
                imageNameMap = imageNameMap,
                tileSettings = gameObject.tileSettings,
                matrix = matrix,
                characterResNames = characterResNames
            )
        }
    }
}