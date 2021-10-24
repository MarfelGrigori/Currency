package com.example.restapi.networking

import com.example.restapi.data.dto.currency.RateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {

    @GET("api/exrates/rates")
    suspend fun loadRates(
        @Query("periodicity")
        periodicity: Int
    ): Response<List<RateResponse>>

    @GET("api/exrates/rates")
    suspend fun loadRatesForTomorrow(
        @Query("ondate")
        date: String,
        @Query("periodicity")
        periodicity: Int,
    ): Response<List<RateResponse>>
}