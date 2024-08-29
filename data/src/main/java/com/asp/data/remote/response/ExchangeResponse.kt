package com.asp.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeResponse (
    val disclaimer: String? = null,
    val license: String? = null,
    val timestamp: Long? = 0,
    val base: String? = null, // USD generally
    val rates: Map<String, Double>? = null
)