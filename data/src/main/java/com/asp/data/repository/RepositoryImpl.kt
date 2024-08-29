package com.asp.data.repository

import com.asp.data.datastore.CurrencyDataStore
import com.asp.data.local.cache.CacheStrategy
import com.asp.data.mapper.CurrencyMapper
import com.asp.data.mapper.ExchangeMapper
import com.asp.domain.repository.Repository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val localStore: CurrencyDataStore.CurrencyLocalDataStore,
    private val remoteStore: CurrencyDataStore.CurrencyRemoteDataStore,
    private val cacheStrategy: CacheStrategy,
    private val currencyMapper: CurrencyMapper,
    private val exchangeMapper: ExchangeMapper
): Repository {

    override fun getExchangeRates(appId: String, shouldForceRefresh: Boolean) = flow {
        try {
            val result = localStore.getExchangeRates().let { local ->
                if (local.isNullOrEmpty() || (local.isNullOrEmpty().not() && cacheStrategy.shouldRefresh(local.first().timestamp, shouldForceRefresh))) {
                    val currentTimeStamp = System.currentTimeMillis()
                    // Not clearing the old data before insertion as expecting the currencies to not change and we have Conflict resolution strategy to update old data on new writes
                    val remoteExchange = exchangeMapper.mapResponse(remoteStore.getExchangeRates(appId)).map {
                        it.copy(timestamp = currentTimeStamp)
                    }
                    localStore.putExchangeRates(remoteExchange)
                    remoteExchange
                } else {
                    local
                }
            }
            emit(Result.success(exchangeMapper.mapEntity(result)))
        } catch (ex: Exception) {
            emit(Result.failure(ex))
        }
    }

    override fun getCurrencies() = flow {
        try {
            val result = localStore.getCurrencies().let { local ->
                // Not adding logic for timely update on currencies, expecting it to be an information that does not change frequently
                local.ifEmpty {
                    // Not clearing the old data before insertion as expecting the currencies to not change and we have Conflict resolution strategy to update old data on new writes
                    val currencyEntities = currencyMapper.mapResponse(remoteStore.getCurrencies())
                    localStore.putCurrencies(currencyEntities)
                    currencyEntities
                }
            }
            emit(Result.success(currencyMapper.mapEntity(result)))
        } catch (ex: Exception) {
            emit(Result.failure(ex))
        }
    }

}