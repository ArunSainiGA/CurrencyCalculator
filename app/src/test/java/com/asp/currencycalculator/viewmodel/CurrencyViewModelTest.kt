package com.asp.currencycalculator.viewmodel

import com.asp.currencycalculator.BaseViewModelTest
import com.asp.currencycalculator.viewmodel.currency.CurrencyViewModel
import com.asp.domain.model.CurrencyModel
import com.asp.domain.model.ExchangeModel
import com.asp.domain.usecase.GetCurrencyUseCase
import com.asp.domain.usecase.GetExchangeRateUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest: BaseViewModelTest() {

    private lateinit var viewModel: CurrencyViewModel

    private val mockCurrencyUseCase: GetCurrencyUseCase = mockk()
    private val mockExchangeUseCase: GetExchangeRateUseCase = mockk()

    private val mockCurrencyList = listOf(
        CurrencyModel("USD", "United States Dollar"),
        CurrencyModel("INR", "India")
    )

    private val mockExchangeList = listOf(
        ExchangeModel("USD", 1.0),
        ExchangeModel("INR", 0.78)
    )

    @Before
    override fun setUp() {
        super.setUp()
        // Setting this initial mock as we call the api from init block too
        every { mockCurrencyUseCase.invoke() } returns flow {
            Result.success(emptyList<CurrencyModel>())
        }
        viewModel = CurrencyViewModel(mockCurrencyUseCase, mockExchangeUseCase)
    }

    @Test
    fun getCurrencyList_success_currencyListUpdated() = runTest {
        var currencyList: Array<String>? = null

        every { mockCurrencyUseCase.invoke() } returns flow {
            emit(Result.success(mockCurrencyList))
        }

        val expectedList = mockCurrencyList.map { it.currencyCode }.toTypedArray()

        viewModel.currenciesState.observe(lifeCycleOwner) {
            currencyList = it.data
        }

        viewModel.getCurrencyList()

        advanceUntilIdle()

        Assert.assertArrayEquals(expectedList, currencyList)
    }

    @Test
    fun getCurrencyList_failed_errorUpdated() = runTest {
        var currencyList: Array<String>? = null
        var error: Throwable? = null

        every { mockCurrencyUseCase.invoke() } returns flow {
            emit(Result.failure(Exception("Some exception message")))
        }

        viewModel.currenciesState.observe(lifeCycleOwner) {
            error = it.error
            currencyList = it.data
        }

        viewModel.getCurrencyList()

        advanceUntilIdle()

        Assert.assertNull(currencyList)
        Assert.assertNotNull(error)
    }

    @Test
    fun getCurrencyConversions_amountIsZero_noExchangeFetched() = runTest {
        viewModel.getCurrencyConversions( "AED", 0.0)

        coVerify(exactly = 0) { mockExchangeUseCase.invoke(any(), any()) }
    }

    @Test
    fun getCurrencyConversions_currencyCodeEmpty_noExchangeFetched() = runTest {
        viewModel.getCurrencyConversions( "", 1.4)

        coVerify(exactly = 0) { mockExchangeUseCase.invoke(any(), any()) }
    }

    @Test
    fun getCurrencyConversions_currencyCodeAndAmountBothInvalid_noExchangeFetched() = runTest {
        viewModel.getCurrencyConversions( "", 0.0)

        coVerify(exactly = 0) { mockExchangeUseCase.invoke(any(), any()) }
    }

    @Test
    fun getCurrencyConversions_validInputsButNoCurrencyCodeMatch_exchangeFetchedSelectedConversionsNull() = runTest {
        var fetchedList: List<ExchangeModel>? = null
        var selectedConversionList: List<ExchangeModel>? = null

        every { mockExchangeUseCase.invoke(any(), any()) } returns flow {
            emit(Result.success(mockExchangeList))
        }

        viewModel.exchangeState.observe(lifeCycleOwner) {
            fetchedList = it.data
        }

        viewModel.selectedConversions.observe(lifeCycleOwner) {
            selectedConversionList = it
        }

        viewModel.getCurrencyConversions("AED", 1.4)

        advanceUntilIdle()

        Assert.assertNotNull(fetchedList)
        Assert.assertEquals(mockExchangeList.count(), fetchedList?.count())
        Assert.assertArrayEquals(mockExchangeList.toTypedArray(), fetchedList?.toTypedArray())
        Assert.assertNull(selectedConversionList)
    }

    @Test
    fun getCurrencyConversions_validInputsButCurrencyCodeMatch_exchangeFetchedSelectedConversionsIsValid() = runTest {
        var fetchedList: List<ExchangeModel>? = null
        var selectedConversionList: List<ExchangeModel>? = null

        every { mockExchangeUseCase.invoke(any(), any()) } returns flow {
            emit(Result.success(mockExchangeList))
        }

        viewModel.exchangeState.observe(lifeCycleOwner) {
            fetchedList = it.data
        }

        viewModel.selectedConversions.observe(lifeCycleOwner) {
            selectedConversionList = it
        }

        viewModel.getCurrencyConversions("INR", 1.4)

        var inrConversion = 0.0
        var usdConversion = 0.0
        mockExchangeList.find { it.currencyCode == "INR" }?.rate?.let {
            inrConversion = 1.4
            usdConversion = 1.0/it * 1.4
        }

        advanceUntilIdle()

        Assert.assertNotNull(fetchedList)
        Assert.assertEquals(mockExchangeList.count(), fetchedList?.count())
        Assert.assertArrayEquals(mockExchangeList.toTypedArray(), fetchedList?.toTypedArray())
        Assert.assertNotNull(selectedConversionList)
        Assert.assertEquals(inrConversion, selectedConversionList?.find { it.currencyCode == "INR" }?.rate)
        Assert.assertEquals(usdConversion, selectedConversionList?.find { it.currencyCode == "USD" }?.rate)
    }

    @Test
    fun getCurrencyConversions_failed_errorUpdated() = runTest {
        var error: Throwable? = null

        every { mockExchangeUseCase.invoke(any(), any()) } returns flow {
            emit(Result.failure(Exception("Some exception message")))
        }

        viewModel.exchangeState.observe(lifeCycleOwner) {
            error = it.error
        }

        viewModel.getCurrencyConversions("INR", 1.4)

        advanceUntilIdle()

        Assert.assertNotNull(error)
    }
}