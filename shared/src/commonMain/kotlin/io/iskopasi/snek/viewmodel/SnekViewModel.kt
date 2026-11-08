package io.iskopasi.snek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.snek.model.GameObject
import io.iskopasi.snek.model.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UIState(
    val score: Int,
    val snekBody: ArrayDeque<Point>,
    val foodPoints: Set<Point>,
    val mapSize: Int = 10,
)

class SnekViewModel(val mapSize: Int = 10) : ViewModel() {
    private val gameObject = GameObject(size = mapSize)
    private val _state = MutableStateFlow(
        UIState(
            score = 0,
            snekBody = gameObject.snekData.body,
            foodPoints = gameObject.foodMap.keys,
            mapSize = mapSize
        )
    )
    val state = _state.asStateFlow()

    fun moveUp() {
        viewModelScope.launch {
            gameObject.moveUp()

            update()
        }
    }

    fun moveDown() {
        viewModelScope.launch {
            gameObject.moveDown()

            update()
        }
    }

    fun moveLeft() {
        viewModelScope.launch {
            gameObject.moveLeft()

            update()
        }
    }

    fun moveRight() {
        viewModelScope.launch {
            gameObject.moveRight()

            update()
        }
    }

    private fun update() {
        _state.update {
            it.copy(
                score = gameObject.score,
                snekBody = gameObject.snekData.body,
                foodPoints = gameObject.foodMap.keys
            )
        }
    }
}