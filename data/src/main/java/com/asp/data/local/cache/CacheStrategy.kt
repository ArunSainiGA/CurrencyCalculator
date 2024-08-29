package com.asp.data.local.cache

interface CacheStrategy {
    val refreshInterval: Long

    fun shouldRefresh(lastRefreshTime: Long, forceRefresh: Boolean): Boolean {
        return forceRefresh || (System.currentTimeMillis() - lastRefreshTime) >= refreshInterval
    }
}