package com.asp.data.mapper

import com.asp.data.local.entity.CurrencyEntity
import org.junit.Assert
import org.junit.Test

class CurrencyMapperTest {
    private val sut = CurrencyMapper()

    @Test
    fun mapResponse_whenInputIsCorrect_returnsCorrectOutput() {
        val input = mapOf(
            "AED" to "United Arab Emirates Dirham",
            "AFN" to "Afghan Afghani"
        )
        val output = sut.mapResponse(input)
        Assert.assertEquals(input.count(), output.count())
        // Order is maintained and data is correct
        Assert.assertEquals(input["AED"], output.first().currencyName)
    }

    @Test
    fun mapResponse_whenInputIsEmpty_returnsEmptyOutput() {
        val input = emptyMap<String, String>()
        val output = sut.mapResponse(input)
        Assert.assertTrue(output.isEmpty())
    }

    @Test
    fun mapEntity_whenInputIsCorrect_returnsCorrectOutput() {
        val input = listOf(
            CurrencyEntity(currencyCode = "AED", currencyName = "United Arab Emirates Dirham"),
            CurrencyEntity(currencyCode = "AFN", currencyName = "Afghan Afghani")
        )
        val output = sut.mapEntity(input)
        Assert.assertEquals(input.count(), output.count())
        // Order is maintained and data is correct
        Assert.assertEquals(input.first().currencyCode, output.first().currencyCode)
        Assert.assertEquals(input.first().currencyName, output.first().currencyName)
    }

    @Test
    fun mapEntity_whenInputIsEmpty_returnsEmptyOutput() {
        val input = emptyList<CurrencyEntity>()
        val output = sut.mapEntity(input)
        Assert.assertTrue(output.isEmpty())
    }
}