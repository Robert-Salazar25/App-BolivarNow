package com.example.convertapp.presentation.ui.screens

import com.example.convertapp.presentation.viewmodel.CurrencyViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.convertapp.presentation.ui.components.ErrorCard
import com.example.convertapp.presentation.ui.components.LoadingCard

@Composable

fun CurrencyConverterScreen(viewModel: CurrencyViewModel = viewModel()){
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        CurrencyConverterUI(viewModel = viewModel)

        // Overlay para estados de carga/error
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                LoadingCard({showDialog = false})
            }
        }

        uiState.error?.let { errorMessage ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(
                    message = errorMessage,
                    onRetry = { viewModel.loadRates() },
                    onDismiss = {showDialog = false}
                )
            }
        }
    }
}