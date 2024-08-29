package com.asp.domain.model

data class ExchangeModel (
    val currencyCode: String,
    val rate: Double
) {
    fun getRateString(): String {
        return rate.toString()
    }
}