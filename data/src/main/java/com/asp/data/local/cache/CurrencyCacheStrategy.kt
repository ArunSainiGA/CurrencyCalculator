package com.asp.data.local.cache

import javax.inject.Inject

// NOTE: Ideally in real project we would have multiple type of data and cache so the injection should be annotation based or named
class CurrencyCacheStrategy @Inject constructor() : CacheStrategy {
    override val refreshInterval: Long = 30 * 60 * 1000 // 30 minutes
}