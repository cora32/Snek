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