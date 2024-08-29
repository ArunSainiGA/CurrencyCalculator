package com.asp.domain.repository

import com.asp.domain.model.CurrencyModel
import com.asp.domain.model.ExchangeModel
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getExchangeRates(appId: String, shouldForceRefresh: Boolean = false): Flow<Result<List<ExchangeModel>>>
    fun getCurrencies(): Flow<Result<List<CurrencyModel>>>
}