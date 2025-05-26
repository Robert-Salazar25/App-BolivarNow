package com.example.convertapp.data.api

import CurrencyResponse
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("dollar")
    suspend fun getDollarRates(): CurrencyResponse

    @GET("euro")
    suspend fun getEuroRates(): CurrencyResponse
}