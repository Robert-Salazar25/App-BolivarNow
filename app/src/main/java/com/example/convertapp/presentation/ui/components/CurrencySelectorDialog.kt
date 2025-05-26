package com.example.convertapp.presentation.ui.components

import com.example.convertapp.presentation.viewmodel.CurrencyViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CurrencySelectorDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: CurrencyViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estados temporales
    var tempSelectedCurrency by remember { mutableStateOf(uiState.selectedCurrency) }
    var tempSelectedMonitor by remember { mutableStateOf(uiState.selectedMonitor) } // Solo uno global

    // Inicializar al abrir el diálogo
    LaunchedEffect(showDialog) {
        if (showDialog) {
            tempSelectedCurrency = uiState.selectedCurrency
            tempSelectedMonitor = uiState.selectedMonitor
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            }
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .heightIn(min = 200.dp, max = 400.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selecciona la moneda y monitor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de moneda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("USD", "EUR").forEach { currency ->
                            FilterChip(
                                selected = tempSelectedCurrency == currency,
                                onClick = {
                                    tempSelectedCurrency = currency
                                },
                                label = { Text(currency) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de monitores
                    val currentMonitors = viewModel.getAvailableMonitorsForCurrency(tempSelectedCurrency)

                    if (currentMonitors.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay monitores disponibles", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 280.dp)
                                .weight(1f, fill = false)
                        ) {
                            items(currentMonitors) { (monitorId, monitor) ->
                                MonitorItem(
                                    monitor = monitor,
                                    isSelected = monitorId == tempSelectedMonitor,
                                    onClick = {
                                        tempSelectedMonitor = monitorId
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de confirmación
                    Button(
                        onClick = {
                            viewModel.setSelectedCurrency(tempSelectedCurrency)
                            viewModel.setSelectedMonitor(tempSelectedMonitor)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = tempSelectedMonitor.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Confirmar selección")
                    }
                }
            }
        }
    }
}

