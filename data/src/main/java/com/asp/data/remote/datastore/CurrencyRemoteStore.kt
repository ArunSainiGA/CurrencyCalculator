package com.asp.data.remote.datastore

import com.asp.data.datastore.CurrencyDataStore
import com.asp.data.remote.response.ExchangeResponse
import com.asp.data.remote.service.CurrencyService
import javax.inject.Inject

class CurrencyRemoteStore @Inject constructor(
    private val currencyService: CurrencyService
): CurrencyDataStore.CurrencyRemoteDataStore {
    override suspend fun getCurrencies(): Map<String, String> {
        return currencyService.getCurrencies()
    }

    override suspend fun getExchangeRates(appId: String): ExchangeResponse {
        return currencyService.getExchangeRates(appId)
    }
}