package com.selffocus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selffocus.core.constants.AppConstants
import com.selffocus.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val focusModeEnabled: Boolean = false,
    val version: String = "1.0.0"
)

/**
 * UI Events for Settings screen.
 */
sealed class SettingsUiEvent {
    data class ShowSnackbar(val message: String) : SettingsUiEvent()
}

/**
 * ViewModel for Settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<SettingsUiEvent?>(null)
    val uiEvent: StateFlow<SettingsUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                preferencesRepository.themeMode.collect { mode ->
                    _uiState.value = _uiState.value.copy(themeMode = mode)
                }
            }
            launch {
                preferencesRepository.focusModeEnabled.collect { enabled ->
                    _uiState.value = _uiState.value.copy(focusModeEnabled = enabled)
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode).onSuccess {
                _uiState.value = _uiState.value.copy(themeMode = mode)
                _uiEvent.value = SettingsUiEvent.ShowSnackbar("Tema actualizado")
            }.onFailure { error ->
                _uiEvent.value = SettingsUiEvent.ShowSnackbar("Error: ${error.message}")
            }
        }
    }

    fun setFocusModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFocusModeEnabled(enabled).onSuccess {
                _uiState.value = _uiState.value.copy(focusModeEnabled = enabled)
            }.onFailure { error ->
                _uiEvent.value = SettingsUiEvent.ShowSnackbar("Error: ${error.message}")
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            preferencesRepository.clearAllPreferences().onSuccess {
                _uiEvent.value = SettingsUiEvent.ShowSnackbar("Datos borrados")
                loadSettings()
            }.onFailure { error ->
                _uiEvent.value = SettingsUiEvent.ShowSnackbar("Error: ${error.message}")
            }
        }
    }

    fun consumeEvent() {
        _uiEvent.value = null
    }
}
