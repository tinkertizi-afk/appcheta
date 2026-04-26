package com.selffocus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selffocus.core.constants.AppConstants
import com.selffocus.domain.model.FocusSession
import com.selffocus.domain.usecase.CompleteFocusSession
import com.selffocus.domain.usecase.GetStreakCount
import com.selffocus.domain.usecase.GetTotalFocusSessions
import com.selffocus.domain.usecase.StartFocusSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Focus Timer screen.
 */
data class FocusUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSessionActive: Boolean = false,
    val activeSessionId: Long? = null,
    val durationMinutes: Int = AppConstants.DEFAULT_FOCUS_DURATION_MINUTES,
    val remainingSeconds: Int = 0,
    val totalSessions: Int = 0,
    val streakCount: Int = 0,
    val isCompleted: Boolean = false
)

/**
 * UI Events for Focus Timer screen.
 */
sealed class FocusUiEvent {
    data class ShowSnackbar(val message: String) : FocusUiEvent()
    object SessionComplete : FocusUiEvent()
}

/**
 * ViewModel for Focus Timer screen.
 */
@HiltViewModel
class FocusViewModel @Inject constructor(
    private val startFocusSession: StartFocusSession,
    private val completeFocusSession: CompleteFocusSession,
    private val getTotalFocusSessions: GetTotalFocusSessions,
    private val getStreakCount: GetStreakCount
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<FocusUiEvent?>(null)
    val uiEvent: StateFlow<FocusUiEvent?> = _uiEvent.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            launch {
                getTotalFocusSessions().collect { count ->
                    _uiState.value = _uiState.value.copy(totalSessions = count)
                }
            }
            launch {
                getStreakCount().collect { count ->
                    _uiState.value = _uiState.value.copy(streakCount = count)
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun setDuration(minutes: Int) {
        if (!_uiState.value.isSessionActive) {
            _uiState.value = _uiState.value.copy(
                durationMinutes = minutes.coerceIn(
                    AppConstants.MIN_FOCUS_DURATION_MINUTES,
                    AppConstants.MAX_FOCUS_DURATION_MINUTES
                )
            )
        }
    }

    fun startSession() {
        viewModelScope.launch {
            val duration = _uiState.value.durationMinutes
            
            startFocusSession(duration).onSuccess { sessionId ->
                _uiState.value = _uiState.value.copy(
                    isSessionActive = true,
                    activeSessionId = sessionId,
                    remainingSeconds = duration * 60,
                    isCompleted = false
                )
                startTimer(sessionId, duration * 60)
                _uiEvent.value = FocusUiEvent.ShowSnackbar("Sesión de enfoque iniciada")
            }.onFailure { error ->
                _uiEvent.value = FocusUiEvent.ShowSnackbar("Error al iniciar: ${error.message}")
            }
        }
    }

    private fun startTimer(sessionId: Long, totalSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.value = _uiState.value.copy(remainingSeconds = remaining)
            }
            // Session complete
            completeSession(sessionId)
        }
    }

    fun stopSession() {
        timerJob?.cancel()
        timerJob = null
        
        _uiState.value.activeSessionId?.let { id ->
            viewModelScope.launch {
                // Cancel without marking as completed
                _uiState.value = _uiState.value.copy(
                    isSessionActive = false,
                    activeSessionId = null,
                    remainingSeconds = 0
                )
                _uiEvent.value = FocusUiEvent.ShowSnackbar("Sesión cancelada")
            }
        }
    }

    private fun completeSession(sessionId: Long) {
        viewModelScope.launch {
            completeFocusSession(sessionId).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSessionActive = false,
                    activeSessionId = null,
                    remainingSeconds = 0,
                    isCompleted = true,
                    totalSessions = _uiState.value.totalSessions + 1
                )
                _uiEvent.value = FocusUiEvent.SessionComplete
                _uiEvent.value = FocusUiEvent.ShowSnackbar("¡Sesión completada! 🎉")
                
                // Reset completion flag after showing
                delay(2000)
                _uiState.value = _uiState.value.copy(isCompleted = false)
            }.onFailure { error ->
                _uiEvent.value = FocusUiEvent.ShowSnackbar("Error al completar: ${error.message}")
            }
        }
    }

    fun consumeEvent() {
        _uiEvent.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
