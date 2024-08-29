package com.asp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity

@Dao
interface CurrencyDao {
    // NOTE: from getters we can have Flow type data being sent back so that we can have direct updates and observations,
    // But for the exchanges we want the cache logic so keeping it simple for both getters
    @Query("SELECT * FROM currencies")
    suspend fun getCurrencies() : List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM exchange_rates")
    suspend fun getRates() : List<ExchangeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeEntity>)
}