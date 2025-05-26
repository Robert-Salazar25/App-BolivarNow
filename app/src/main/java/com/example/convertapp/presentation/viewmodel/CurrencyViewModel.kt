package com.example.convertapp.presentation.viewmodel

import CurrencyResponse
import Monitor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.convertapp.domain.repository.CurrencyRepository
import com.example.convertapp.presentation.state.CurrencyUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class CurrencyViewModel(private val repository: CurrencyRepository): ViewModel(){

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState

    //Estados para los datos
    private val _dollarRates = mutableStateOf<CurrencyResponse?>(null)
    private val _EuroRates = mutableStateOf<CurrencyResponse?>(null)

    private val _lastSourceValue = mutableStateOf("1.00")
    private val _lastTargetValue = mutableStateOf("")

    val lastSourceValue: String by _lastSourceValue
    val lastTargetValue: String by _lastTargetValue


    init {
        loadRates()
    }

    fun loadRates() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Cargar tasas en paralelo
                val dollarDeferred = async { repository.getDollarRates() }
                val euroDeferred = async { repository.getEuroRates() }

                val dollarResult = dollarDeferred.await()
                val euroResult = euroDeferred.await()

                dollarResult.fold(
                    onSuccess = { response ->
                        _dollarRates.value = response
                        if (_uiState.value.selectedMonitor.isEmpty() && response.monitors.isNotEmpty()) {
                            _uiState.update { it.copy(selectedMonitor = response.monitors.keys.first()) }
                        }
                    },
                    onFailure = { e ->
                        handleError(e, "dólar")
                    }
                )

                euroResult.fold(
                    onSuccess = { response ->
                        _EuroRates.value = response
                    },
                    onFailure = { e ->
                        handleError(e, "euro")
                    }
                )

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError(e, "general")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleError(exception: Throwable, context: String) {
        val errorMessage = when (exception) {
            is IOException -> {
                when {
                    exception.message?.contains("Unable to resolve host") == true ->
                        "Error de conexión"
                    else -> "Error de red: ${exception.message}"
                }
            }
            is HttpException -> {
                when (exception.code()) {
                    in 400..499 -> {
                        when (exception.code()) {
                            401 -> "Error de autenticación (401)"
                            403 -> "Acceso denegado (403)"
                            404 -> "Recurso no encontrado (404)"
                            else -> "Error del cliente (${exception.code()})"
                        }
                    }
                    in 500..599 -> "Error del servidor (${exception.code()})"
                    else -> "Error HTTP (${exception.code()})"
                }
            }
            else -> "Error al cargar tasas de $context: ${exception.message}"
        }

        _uiState.update { it.copy(error = errorMessage) }
    }




    fun setSelectedCurrency(currency: String){
        _uiState.update { it.copy(selectedCurrency = currency) }

        // Al cambiar de moneda, NO cambiar automáticamente el monitor seleccionado
        // Solo mantenerlo si existe para la nueva moneda
        val monitors = getAvailableMonitorsForCurrency(currency)
        if (monitors.none { it.first == _uiState.value.selectedMonitor }) {
            _uiState.update { it.copy(selectedMonitor = "") } // Limpiar selección si no existe
        }
    }

    fun setSelectedMonitor(monitor: String){
        _uiState.update { it.copy(selectedMonitor = monitor) }
    }

    fun setInputValue(value: String){
        val newValue = when {
            value.isEmpty() -> "0"
            value == "." -> "0."
            value.count { it == '.' } > 1 -> _uiState.value.inputValue
            else -> value
        }

        _uiState.update { it.copy(inputValue = newValue) }

        // Actualizamos el último valor conocido según el modo actual
        if (_uiState.value.isSourceCurrency) {
            _lastSourceValue.value = newValue
        } else {
            _lastTargetValue.value = newValue
        }
    }

    fun tpggleConversionDirection(){
        _uiState.update {
            it.copy(
                isSourceCurrency = !it.isSourceCurrency,
                // Mantenemos el valor actual sin cambios
                inputValue = if (it.isSourceCurrency) lastTargetValue else lastSourceValue
            )
        }
    }

    fun getCurrentRate(): Double?{
        return when(_uiState.value.selectedCurrency){
            "USD" -> _dollarRates.value?.monitors?.get(_uiState.value.selectedMonitor)?.price
            "EUR" -> _EuroRates.value?.monitors?.get(_uiState.value.selectedMonitor)?.price
            else -> null
        }
    }

    fun getConvertedValue(): String{
        val input = _uiState.value.inputValue.toDoubleOrNull() ?: 0.0
        val rate = getCurrentRate() ?: return "0.00"

        val result = if (_uiState.value.isSourceCurrency) {
            "%.2f".format(input * rate)
        } else {
            "%.2f".format(input / rate)
        }

        // Actualizamos el valor opuesto cuando calculamos
        if (_uiState.value.isSourceCurrency) {
            _lastTargetValue.value = result
        } else {
            _lastSourceValue.value = result
        }

        return result
    }

    fun getAvailableMonitors(): List<Pair<String, Monitor>>{
        return when(_uiState.value.selectedCurrency){
            "USD" ->  _dollarRates.value?.monitors?.toList() ?: emptyList()
            "EUR" -> _EuroRates.value?.monitors?.toList() ?: emptyList()
            else -> emptyList()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun getMonitorTitle(monitorId: String): String{
        return when(_uiState.value.selectedCurrency){
            "USD" -> _dollarRates.value?.monitors?.get(monitorId)?.title ?: monitorId
            "EUR" -> _EuroRates.value?.monitors?.get(monitorId)?.title ?: monitorId
            else -> monitorId

        }
    }

    fun getAvailableMonitorsForCurrency(currency: String): List<Pair<String, Monitor>> {
        return when(currency) {
            "USD" -> {
                _dollarRates.value?.monitors?.toList()?.filter {
                    it.second.title.contains("dólar", ignoreCase = true) ||
                            it.first.lowercase().contains("parallel") ||
                            it.first.lowercase().contains("enparalelo")
                } ?: emptyList()
            }
            "EUR" -> {
                _EuroRates.value?.monitors?.toList()?.filter {
                    it.second.title.contains("euro", ignoreCase = true) ||
                            !it.second.title.contains("dólar", ignoreCase = true)
                } ?: emptyList()
            }
            else -> emptyList()
        }
    }

}