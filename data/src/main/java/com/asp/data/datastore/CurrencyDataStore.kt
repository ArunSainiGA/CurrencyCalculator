package com.asp.data.datastore

import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity
import com.asp.data.remote.response.ExchangeResponse

interface CurrencyDataStore {
    interface CurrencyLocalDataStore {
        suspend fun getCurrencies(): List<CurrencyEntity>
        suspend fun putCurrencies(currencies: List<CurrencyEntity>)
        suspend fun getExchangeRates(): List<ExchangeEntity>
        suspend fun putExchangeRates(rates: List<ExchangeEntity>)
    }

    interface CurrencyRemoteDataStore {
        suspend fun getCurrencies(): Map<String, String>
        suspend fun getExchangeRates(appId: String): ExchangeResponse
    }
}