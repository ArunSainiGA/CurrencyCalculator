package com.asp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.asp.data.local.CurrencyDB
import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyDaoTest {
    private lateinit var db: CurrencyDB
    private lateinit var currencyDao: CurrencyDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDB::class.java
        ).build()
        currencyDao = db.getCurrencyDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertCurrency() = runBlocking {
        val currencies = listOf(
            CurrencyEntity(currencyCode = "USD", currencyName = "United States Dollar"),
            CurrencyEntity(currencyCode = "INR", currencyName = "India")
        )
        currencyDao.insertCurrencies(currencies)
        val localCurrencies = currencyDao.getCurrencies()

        Assert.assertTrue(localCurrencies.count() == 2)
    }

    @Test
    fun insertCurrency_conflict() = runBlocking {
        val currencies = listOf(
            CurrencyEntity(currencyCode = "USD", currencyName = "United States Dollar"),
            CurrencyEntity(currencyCode = "INR", currencyName = "India")
        )
        currencyDao.insertCurrencies(currencies)
        // Inserting again with same data
        currencyDao.insertCurrencies(currencies)
        val localCurrencies = currencyDao.getCurrencies()

        Assert.assertTrue(localCurrencies.count() == 2)
    }

    @Test
    fun insertRates() = runBlocking {
        val exchangeRates = listOf(
            ExchangeEntity(currencyCode = "USD", rate = 1.0, System.currentTimeMillis()),
            ExchangeEntity(currencyCode = "INR", rate = 0.78, System.currentTimeMillis())
        )
        currencyDao.insertRates(exchangeRates)
        val localExchangeRates = currencyDao.getRates()

        Assert.assertTrue(localExchangeRates.count() == 2)
    }

    @Test
    fun insertRates_conflict() = runBlocking {
        val exchangeRates = listOf(
            ExchangeEntity(currencyCode = "USD", rate = 1.0, System.currentTimeMillis()),
            ExchangeEntity(currencyCode = "INR", rate = 0.78, System.currentTimeMillis())
        )
        currencyDao.insertRates(exchangeRates)
        // Inserting again with same data
        currencyDao.insertRates(exchangeRates)
        val localExchangeRates = currencyDao.getRates()

        Assert.assertTrue(localExchangeRates.count() == 2)
    }
}