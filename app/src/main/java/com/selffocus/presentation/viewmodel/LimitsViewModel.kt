package com.selffocus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selffocus.domain.model.DailyLimit
import com.selffocus.domain.usecase.GetAllLimits
import com.selffocus.domain.usecase.SetAppLimit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for App Limits screen.
 */
data class LimitsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val limits: List<DailyLimit> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

/**
 * UI Events for App Limits screen.
 */
sealed class LimitsUiEvent {
    data class ShowSnackbar(val message: String) : LimitsUiEvent()
    object NavigateBack : LimitsUiEvent()
}

/**
 * ViewModel for App Limits screen.
 */
@HiltViewModel
class LimitsViewModel @Inject constructor(
    private val getAllLimits: GetAllLimits,
    private val setAppLimit: SetAppLimit
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitsUiState())
    val uiState: StateFlow<LimitsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<LimitsUiEvent?>(null)
    val uiEvent: StateFlow<LimitsUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadLimits()
    }

    fun loadLimits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getAllLimits().onSuccess { limits ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    limits = limits
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar límites"
                )
            }
        }
    }

    fun saveLimit(packageName: String, appName: String, minutes: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            setAppLimit(packageName, appName, minutes).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
                _uiEvent.value = LimitsUiEvent.ShowSnackbar("Límite guardado correctamente")
                loadLimits() // Reload to show updated limit
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = false
                )
                _uiEvent.value = LimitsUiEvent.ShowSnackbar("Error al guardar: ${error.message}")
            }
        }
    }

    fun deleteLimit(id: Long) {
        // TODO: Implement delete use case
        _uiEvent.value = LimitsUiEvent.ShowSnackbar("Función no implementada")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumeEvent() {
        _uiEvent.value = null
    }
}
