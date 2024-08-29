package com.asp.data.local.datastore

import com.asp.data.datastore.CurrencyDataStore
import com.asp.data.local.dao.CurrencyDao
import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity
import javax.inject.Inject

class CurrencyLocalStore @Inject constructor(
    private val currencyDao: CurrencyDao
): CurrencyDataStore.CurrencyLocalDataStore{
    override suspend fun getCurrencies(): List<CurrencyEntity> {
        return currencyDao.getCurrencies()
    }

    override suspend fun putCurrencies(currencies: List<CurrencyEntity>) {
        currencyDao.insertCurrencies(currencies)
    }

    override suspend fun getExchangeRates(): List<ExchangeEntity> {
        return currencyDao.getRates()
    }

    override suspend fun putExchangeRates(rates: List<ExchangeEntity>) {
        currencyDao.insertRates(rates)
    }
}