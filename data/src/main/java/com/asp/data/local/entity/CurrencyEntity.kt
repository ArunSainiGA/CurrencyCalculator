package com.asp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies", primaryKeys = ["currencyCode"])
data class CurrencyEntity(
    val currencyCode: String,
    val currencyName: String
)
