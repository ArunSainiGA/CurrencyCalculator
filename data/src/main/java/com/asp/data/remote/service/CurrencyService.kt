package com.asp.data.remote.service

import com.asp.data.remote.response.ExchangeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET(RATES)
    suspend fun getExchangeRates(
        @Query(QUERY_PARAM_APP_ID) appId: String // Should be coming from application for better security and reusability of the data layer
    ): ExchangeResponse

    @GET(CURRENCIES)
    suspend fun getCurrencies(): Map<String, String>

    companion object {
        const val CURRENCIES = "currencies.json"
        const val RATES = "latest.json"

        const val QUERY_PARAM_APP_ID = "app_id"
    }

}