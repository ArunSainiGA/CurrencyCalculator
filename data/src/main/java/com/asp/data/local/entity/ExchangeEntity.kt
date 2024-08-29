package com.asp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates", primaryKeys = ["currencyCode"])
data class ExchangeEntity (
    val currencyCode: String,
    val rate: Double,
    val timestamp: Long = 0
)