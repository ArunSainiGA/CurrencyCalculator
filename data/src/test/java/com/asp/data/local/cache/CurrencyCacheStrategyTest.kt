package com.asp.data.local.cache

import org.junit.Assert
import org.junit.Test

class CurrencyCacheStrategyTest {
    private val sut = CurrencyCacheStrategy()

    @Test
    fun shouldRefresh_whenForceRefreshIsTrue_returnsTrue() {
        val shouldRefresh = sut.shouldRefresh(System.currentTimeMillis(), true)
        Assert.assertTrue(shouldRefresh)
    }

    @Test
    fun shouldRefresh_whenTimeGapIsMoreThanRefreshInterval_returnsTrue() {
        val shouldRefresh = sut.shouldRefresh(System.currentTimeMillis() - 45*60*1000, false)
        Assert.assertTrue(shouldRefresh)
    }

    @Test
    fun shouldRefresh_whenTimeGapIsMoreThanRefreshIntervalAndForceRefreshIsTrue_returnsTrue() {
        val shouldRefresh = sut.shouldRefresh(System.currentTimeMillis() + 45*60*1000, true)
        Assert.assertTrue(shouldRefresh)
    }

    @Test
    fun shouldRefresh_whenTimeGapIsLessThanRefreshIntervalAndForceRefreshIsTrue_returnsTrue() {
        val shouldRefresh = sut.shouldRefresh(System.currentTimeMillis() + 28*60*1000, true)
        Assert.assertTrue(shouldRefresh)
    }

    @Test
    fun shouldRefresh_whenTimeGapIsLessThanRefreshIntervalAndForceRefreshIsFalse_returnsFalse() {
        val shouldRefresh = sut.shouldRefresh(System.currentTimeMillis() + 28*60*1000, false)
        Assert.assertFalse(shouldRefresh)
    }
}