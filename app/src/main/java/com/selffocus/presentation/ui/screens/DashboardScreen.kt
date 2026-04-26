package com.selffocus.presentation.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selffocus.presentation.ui.components.EmptyState
import com.selffocus.presentation.ui.components.SummaryCard
import com.selffocus.presentation.ui.components.UsageStatCard
import com.selffocus.presentation.viewmodel.DashboardUiEvent
import com.selffocus.presentation.viewmodel.DashboardViewModel

/**
 * Dashboard screen showing usage statistics and summary.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToLimits: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DashboardUiEvent.NavigateToSettings -> {
                    // Open system settings for usage access
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
                else -> {}
            }
            viewModel.consumeEvent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SelfFocus", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToLimits,
                    icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    label = { Text("Límites") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFocus,
                    icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    label = { Text("Enfoque") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Summary Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Tiempo Total",
                        value = uiState.totalScreenTime,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Apps Usadas",
                        value = uiState.appsUsedCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Apps Section
                Text(
                    text = "Apps Más Usadas Hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.recentApps.isEmpty()) {
                    EmptyState(message = "No hay datos de uso disponibles. Concede permiso en Ajustes.")
                } else {
                    uiState.recentApps.take(5).forEach { app ->
                        Spacer(modifier = Modifier.height(8.dp))
                        UsageStatCard(
                            appName = app.appName,
                            usageTime = app.getFormattedTime(),
                            percentage = 0.5f // TODO: Calculate actual percentage
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Permission Notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.warningContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Para ver estadísticas, concede permiso de 'Estadísticas de uso' en Ajustes del sistema.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
