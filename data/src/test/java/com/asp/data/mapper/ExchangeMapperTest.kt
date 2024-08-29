package com.asp.data.mapper

import com.asp.data.local.entity.ExchangeEntity
import com.asp.data.remote.response.ExchangeResponse
import org.junit.Assert
import org.junit.Test

class ExchangeMapperTest {
    private val sut = ExchangeMapper()

    @Test
    fun mapResponse_whenInputIsCorrect_returnsCorrectOutput() {
        val input = ExchangeResponse(
            "Disclaimer",
            "License",
            System.currentTimeMillis(),
            "USD", // USD generally
            mapOf(
                "AED" to 18.2,
                "AFN" to 34.9
            )
        )
        val output = sut.mapResponse(input)
        Assert.assertEquals(input.rates?.count(), output.count())
        // Order is maintained and data is correct
        Assert.assertEquals(input.rates?.get("AED"), output.first().rate)
    }

    @Test
    fun mapResponse_whenInputIsEmpty_returnsEmptyOutput() {
        val input = ExchangeResponse()
        val output = sut.mapResponse(input)
        Assert.assertTrue(output.isEmpty())
    }

    @Test
    fun mapEntity_whenInputIsCorrect_returnsCorrectOutput() {
        val input = listOf(
            ExchangeEntity(
                "AED",
                18.2
            ),
            ExchangeEntity(
                "USD",
                1.0,
            )
        )
        val output = sut.mapEntity(input)
        Assert.assertEquals(input.count(), output.count())
        // Order is maintained and data is correct
        Assert.assertEquals(input.first().currencyCode, output.first().currencyCode)
    }

    @Test
    fun mapEntity_whenInputIsEmpty_returnsEmptyOutput() {
        val input = emptyList<ExchangeEntity>()
        val output = sut.mapEntity(input)
        Assert.assertTrue(output.isEmpty())
    }
}