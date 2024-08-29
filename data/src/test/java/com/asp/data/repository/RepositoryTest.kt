package com.asp.data.repository

import com.asp.data.local.cache.CurrencyCacheStrategy
import com.asp.data.local.dao.CurrencyDao
import com.asp.data.local.datastore.CurrencyLocalStore
import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity
import com.asp.data.mapper.CurrencyMapper
import com.asp.data.mapper.ExchangeMapper
import com.asp.data.remote.datastore.CurrencyRemoteStore
import com.asp.data.remote.response.ExchangeResponse
import com.asp.data.remote.service.CurrencyService
import com.asp.domain.model.CurrencyModel
import com.asp.domain.model.ExchangeModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class RepositoryTest {
    // Note: DAO tests are present in androidTest package so only mocking the DAO in the repo test
    // Also, we can use MockServer to mock responses but keeping it simple and mocking service responses.

    // Also, CacheStrategy is tested separately as a Unit, so not focusing much on it here, still covering the forceRefresh and timeExpire tests

    private val service: CurrencyService = mockk()
    private val currencyDao: CurrencyDao = mockk()

    private val currencyMapper = CurrencyMapper()
    private val exchangeMapper = ExchangeMapper()

    private val sut = RepositoryImpl(
        CurrencyLocalStore(currencyDao),
        CurrencyRemoteStore(service),
        CurrencyCacheStrategy(),
        currencyMapper,
        exchangeMapper
    )

    private val mockCurrencyResponse = mapOf(
        "USD" to "United States Dollar",
        "INR" to "India"
    )

    private val mockCurrencyEntity = listOf(
        CurrencyEntity(currencyCode = "USD", currencyName = "United States Dollar"),
        CurrencyEntity(currencyCode = "INR", currencyName = "India")
    )

    private val mockExchangeResponse = ExchangeResponse(
        "", "", System.currentTimeMillis(), "",
        rates = mapOf(
            "USD" to 1.0,
            "INR" to 0.78
        )
    )

    private val mockExchangeEntity = listOf(
        ExchangeEntity(currencyCode = "USD", rate = 1.0, System.currentTimeMillis()),
        ExchangeEntity(currencyCode = "INR", rate = 0.78, System.currentTimeMillis())
    )

    @Test
    fun getCurrencies_noLocalCurrenciesAvailable_remoteCurrenciesAvailable() {
        var successRemoteResponse: List<CurrencyModel>? = null

        coEvery { currencyDao.getCurrencies() } returns emptyList()
        coEvery { service.getCurrencies() } returns mockCurrencyResponse
        coEvery { currencyDao.insertCurrencies(any()) } just runs

        runTest {
            val result = sut.getCurrencies()
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getCurrencies() }
        coVerify(exactly = 1) { service.getCurrencies() }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockCurrencyResponse.count(), successRemoteResponse?.count())
    }

    @Test
    fun getCurrencies_localCurrenciesAvailable() {
        var successRemoteResponse: List<CurrencyModel>? = null

        coEvery { currencyDao.getCurrencies() } returns mockCurrencyEntity

        runTest {
            val result = sut.getCurrencies()
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getCurrencies() }
        coVerify(exactly = 0) { service.getCurrencies() }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockCurrencyEntity.count(), successRemoteResponse?.count())
    }

    @Test
    fun getCurrencies_localCurrenciesException_errorReturned() {
        var result: Result<List<CurrencyModel>>? = null

        coEvery { currencyDao.getCurrencies() } throws Exception("Some Exception")

        runTest {
            sut.getCurrencies().collect {
                result = it
            }
        }

        coVerify(exactly = 1) { currencyDao.getCurrencies() }
        coVerify(exactly = 0) { service.getCurrencies() }

        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isFailure == true)
    }

    @Test
    fun getCurrencies_noLocalCurrenciesAvailable_remoteCurrenciesException() {
        var result: Result<List<CurrencyModel>>? = null

        coEvery { currencyDao.getCurrencies() } returns emptyList()
        coEvery { service.getCurrencies() } throws Exception("Some Exception")
        coEvery { currencyDao.insertCurrencies(any()) } just runs

        runTest {
            sut.getCurrencies().collect {
                result = it
            }
        }

        coVerify(exactly = 1) { currencyDao.getCurrencies() }
        coVerify(exactly = 1) { service.getCurrencies() }

        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isFailure == true)
    }

    @Test
    fun getExchangeRates_noLocalExchangeAvailable_remoteExchangeAvailable() {
        var successRemoteResponse: List<ExchangeModel>? = null

        coEvery { currencyDao.getRates() } returns emptyList()
        coEvery { service.getExchangeRates(any()) } returns mockExchangeResponse
        coEvery { currencyDao.insertRates(any()) } just runs

        runTest {
            val result = sut.getExchangeRates("", false)
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        coVerify(exactly = 1) { service.getExchangeRates(any()) }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockExchangeResponse.rates?.count(), successRemoteResponse?.count())
    }

    @Test
    fun getExchangeRates_localExchangeAvailableForceRefreshTrue_remoteExchangeAvailable() {
        var successRemoteResponse: List<ExchangeModel>? = null

        coEvery { currencyDao.getRates() } returns mockExchangeEntity
        coEvery { service.getExchangeRates(any()) } returns mockExchangeResponse
        coEvery { currencyDao.insertRates(any()) } just runs

        runTest {
            // Calls api even when local data is available because of shouldForceRefresh. Note, time is within range here
            val result = sut.getExchangeRates("", true)
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        coVerify(exactly = 1) { service.getExchangeRates(any()) }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockExchangeResponse.rates?.count(), successRemoteResponse?.count())
    }


    @Test
    fun getExchangeRates_localExchangeAvailableForceRefreshFalseTimeExpired_remoteExchangeAvailable() {
        var successRemoteResponse: List<ExchangeModel>? = null

        // Mocking time expire behaviour
        coEvery { currencyDao.getRates() } returns mockExchangeEntity.map {
            it.copy(timestamp = System.currentTimeMillis() - 45*60*1000)
        }
        coEvery { service.getExchangeRates(any()) } returns mockExchangeResponse
        coEvery { currencyDao.insertRates(any()) } just runs

        runTest {
            // Calls api even coz time is expired
            val result = sut.getExchangeRates("", false)
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        coVerify(exactly = 1) { service.getExchangeRates(any()) }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockExchangeResponse.rates?.count(), successRemoteResponse?.count())
    }

    @Test
    fun getExchangeRates_localExchangeAvailableWithinTimeForceRefreshFalse_remoteExchangeAvailable() {
        var successRemoteResponse: List<ExchangeModel>? = null

        coEvery { currencyDao.getRates() } returns mockExchangeEntity
        coEvery { service.getExchangeRates(any()) } returns mockExchangeResponse
        coEvery { currencyDao.insertRates(any()) } just runs

        runTest {
            val result = sut.getExchangeRates("", false)
            result.collect {
                it.fold({
                    successRemoteResponse = it
                }, { })
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        // No service calls
        coVerify(exactly = 0) { service.getExchangeRates(any()) }

        Assert.assertNotNull(successRemoteResponse)
        Assert.assertEquals(mockExchangeResponse.rates?.count(), successRemoteResponse?.count())
    }

    @Test
    fun getExchangeRates_localExchangeException_resultIsFailure() {
        var result: Result<List<ExchangeModel>>? = null

        coEvery { currencyDao.getRates() } throws Exception("Some Exception")

        runTest {
            sut.getExchangeRates("", false).collect {
                result = it
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        coVerify(exactly = 0) { service.getExchangeRates("") }

        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isFailure == true)
    }

    @Test
    fun getExchangeRates_remoteExchangeException_resultIsFailure() {
        var result: Result<List<ExchangeModel>>? = null

        coEvery { currencyDao.getRates() } returns mockExchangeEntity
        coEvery { service.getExchangeRates(any()) } throws Exception("Some Exception")

        runTest {
            sut.getExchangeRates("", true).collect {
                result = it
            }
        }

        coVerify(exactly = 1) { currencyDao.getRates() }
        coVerify(exactly = 1) { service.getExchangeRates("") }

        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isFailure == true)
    }
}