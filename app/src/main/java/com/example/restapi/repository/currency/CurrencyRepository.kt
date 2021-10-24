package com.example.restapi.repository.currency

import com.example.restapi.data.entities.currency.Rate
import com.example.restapi.mappers.currency.RateResponseMapper
import com.example.restapi.networking.currency.CurrencyApi


class CurrencyRepository {

    private val api = CurrencyApi.provideRetrofit()
    private val rateResponseMapper = RateResponseMapper()
    suspend fun loadCurrency(periodicity: Int): List<Rate> {
        val response = api.loadRates(periodicity)
        return if (response.isSuccessful) {
            response.body()?.map {
                rateResponseMapper.map(it)
            }.orEmpty()
        } else {
            throw Throwable(response.errorBody().toString())
        }
    }
    suspend fun loadCurrencyTomorrow (periodicity: Int,date : String): List<Rate>{
        val response = api.loadRatesForTomorrow(date,periodicity)
        return if (response.isSuccessful) {
            response.body()?.map {
                rateResponseMapper.map(it)
            }.orEmpty()
        } else {
            throw Throwable(response.errorBody().toString())
        }
    }

}