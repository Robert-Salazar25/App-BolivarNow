package com.example.convertapp.domain.repository

import CurrencyResponse

interface CurrencyRepository{
    suspend fun getDollarRates(): Result<CurrencyResponse>
    suspend fun getEuroRates(): Result<CurrencyResponse>
}