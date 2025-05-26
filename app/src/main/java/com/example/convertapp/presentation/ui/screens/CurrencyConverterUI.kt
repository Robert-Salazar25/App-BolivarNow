package com.example.convertapp.presentation.ui.screens

import com.example.convertapp.presentation.viewmodel.CurrencyViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.convertapp.presentation.ui.components.CurrencySelectorDialog
import com.example.convertapp.R


@Composable
fun CurrencyConverterUI(viewModel: CurrencyViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val animationAlpha by animateFloatAsState(
        targetValue = if (uiState.isSourceCurrency) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "usdAlpha"
    )
    val animatedBsAlpha by animateFloatAsState(
        targetValue = if(!uiState.isSourceCurrency) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "bsAlpha"
    )
    val sourceSize by animateIntAsState(
        targetValue = if (uiState.isSourceCurrency) 20 else 16,
        animationSpec = tween(durationMillis = 300),
        label = "sourceSize"
    )
    val targetSize by animateIntAsState(
        targetValue = if (!uiState.isSourceCurrency) 20 else 16,
        animationSpec = tween(durationMillis = 300),
        label = "targetSize"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("BolivarNow", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            Icon(
                painter = painterResource(R.drawable.ic_sync) ,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.clickable { viewModel.loadRates() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Icono de moneda que abre selector
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.onPrimary, shape = CircleShape)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (uiState.selectedCurrency == "USD") "$" else "€",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conversion result
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor principal para texto e icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                // Columna para los textos alineados
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        if (uiState.isSourceCurrency) "${uiState.inputValue} ${uiState.selectedCurrency}"
                        else "${viewModel.getConvertedValue()} ${uiState.selectedCurrency}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = sourceSize.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(animationAlpha),
                        textAlign = TextAlign.End
                    )

                    Text(
                        if (!uiState.isSourceCurrency) "${uiState.inputValue} Bs"
                        else "${viewModel.getConvertedValue()} Bs",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = targetSize.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(animatedBsAlpha),
                        textAlign = TextAlign.End
                    )
                }

                // Espaciado entre textos e icono
                Spacer(modifier = Modifier.width(8.dp))

                // Icono de intercambio
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .shadow(elevation = 8.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(8.dp))
                        .clickable { viewModel.tpggleConversionDirection() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_swap_vert),
                        contentDescription = "Swap currencies",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Keypad section
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf(".", "0", "⌫")
                )

                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.9f else 1f,
                                animationSpec = spring(), label = "scaleAnim"
                            )

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .scale(scale)
                                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        var newValue = uiState.inputValue

                                        when {
                                            key == "⌫" -> {
                                                newValue = newValue.dropLast(1)
                                                if (newValue.isEmpty()) newValue = "0"
                                            }
                                            key == "." -> {
                                                if (!newValue.contains(".")) {
                                                    if (newValue == "0") newValue = "0."
                                                    else newValue += key
                                                }
                                            }
                                            newValue == "0" -> newValue = key
                                            else -> newValue += key
                                        }

                                        viewModel.setInputValue(newValue)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    key,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    CurrencySelectorDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        viewModel = viewModel
    )
}
