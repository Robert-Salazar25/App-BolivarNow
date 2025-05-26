package com.example.convertapp.presentation.state

data class CurrencyUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCurrency: String = "USD",
    val selectedMonitor: String = "",
    val inputValue: String = "1.00",
    val isSourceCurrency: Boolean = true,
    val showDialog: Boolean = false
)
