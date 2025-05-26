package com.example.convertapp.presentation.ui

import com.example.convertapp.presentation.viewmodel.CurrencyViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.example.convertapp.presentation.theme.ConvertAppTheme
import com.example.convertapp.presentation.viewmodel.CurrencyViewModelFactory
import com.example.convertapp.data.repository.CurrencyRepositoryImpl
import com.example.convertapp.data.network.RetrofitClient
import com.example.convertapp.presentation.ui.screens.CurrencyConverterScreen
import com.example.convertapp.presentation.ui.screens.CurrencyConverterUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crear las dependencias necesarias
        val repository = CurrencyRepositoryImpl(RetrofitClient.currencyApi)
        val viewModelFactory = CurrencyViewModelFactory(repository)

        setContent {
            ConvertAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),

                    color = MaterialTheme.colorScheme.background
                ) {
                    CurrencyConverterApp(viewModelFactory)
                }
            }
        }
    }
}

@Composable
fun CurrencyConverterApp(viewModelFactory: CurrencyViewModelFactory) {
    val viewModel: CurrencyViewModel = viewModel(factory = viewModelFactory)
    CurrencyConverterScreen(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ConvertAppTheme {
        // Para el preview, usamos un ViewModel sin dependencias reales
        CurrencyConverterUI()
    }
}