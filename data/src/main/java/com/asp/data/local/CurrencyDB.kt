package com.asp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asp.data.local.dao.CurrencyDao
import com.asp.data.local.entity.CurrencyEntity
import com.asp.data.local.entity.ExchangeEntity

@Database(entities = [CurrencyEntity::class, ExchangeEntity::class], version = 1, exportSchema = false)
abstract class CurrencyDB: RoomDatabase() {
    abstract fun getCurrencyDao(): CurrencyDao
}