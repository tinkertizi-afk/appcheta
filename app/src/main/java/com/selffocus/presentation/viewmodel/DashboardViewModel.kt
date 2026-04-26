package com.selffocus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selffocus.core.util.Resource
import com.selffocus.domain.model.AppUsageRecord
import com.selffocus.domain.model.UsageSummary
import com.selffocus.domain.usecase.FetchUsageStats
import com.selffocus.domain.usecase.GetDailyUsageData
import com.selffocus.domain.usecase.GetUsageSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Dashboard screen.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val usageSummary: UsageSummary? = null,
    val recentApps: List<AppUsageRecord> = emptyList(),
    val dailyData: List<com.selffocus.domain.model.DailyUsageData> = emptyList(),
    val totalScreenTime: String = "0m",
    val appsUsedCount: Int = 0
)

/**
 * UI Events for Dashboard screen.
 */
sealed class DashboardUiEvent {
    object ShowPermissionRationale : DashboardUiEvent()
    data class ShowSnackbar(val message: String) : DashboardUiEvent()
    data class NavigateToSettings(val reason: String) : DashboardUiEvent()
}

/**
 * ViewModel for Dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchUsageStats: FetchUsageStats,
    private val getUsageSummary: GetUsageSummary,
    private val getDailyUsageData: GetDailyUsageData
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<DashboardUiEvent?>(null)
    val uiEvent: StateFlow<DashboardUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Fetch latest usage stats
            fetchUsageStats().onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar estadísticas: ${error.message}"
                )
            }

            // Load usage summary for today
            val today = java.time.LocalDate.now().toString()
            getUsageSummary(today).collect { result ->
                result.onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usageSummary = summary,
                        recentApps = summary.topApps,
                        totalScreenTime = summary.getFormattedTotalTime(),
                        appsUsedCount = summary.uniqueAppsCount
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
            }

            // Load daily data for chart (7 days)
            getDailyUsageData(7).onSuccess { dailyData ->
                _uiState.value = _uiState.value.copy(dailyData = dailyData)
            }
        }
    }

    fun onEvent(event: DashboardUiEvent) {
        // Events are handled by the UI
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumeEvent() {
        _uiEvent.value = null
    }
}
