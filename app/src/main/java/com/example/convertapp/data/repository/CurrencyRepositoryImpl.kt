package com.example.convertapp.data.repository

import CurrencyResponse
import com.example.convertapp.domain.repository.CurrencyRepository
import com.example.convertapp.data.api.CurrencyApiService

class CurrencyRepositoryImpl(private val apiService: CurrencyApiService): CurrencyRepository {
    override suspend fun getDollarRates(): Result<CurrencyResponse> {
        return try {
            val response = apiService.getDollarRates()
            println("Dollar API Response: $response") // Log para verificar
            Result.success(response)
        } catch (e: Exception) {
            println("Dollar API Error: ${e.message}") // Log para verificar
            Result.failure(e)
        }
    }

    override suspend fun getEuroRates(): Result<CurrencyResponse> {
        return try {
            val response = apiService.getEuroRates()
            println("Euro API Response: $response") // Log para verificar
            Result.success(response)
        } catch (e: Exception) {
            println("Euro API Error: ${e.message}") // Log para verificar
            Result.failure(e)
        }
    }
}