package com.asp.data.mapper

import com.asp.data.local.entity.CurrencyEntity
import com.asp.domain.model.CurrencyModel
import javax.inject.Inject

class CurrencyMapper @Inject constructor(): Mapper<Map<String, String>, List<CurrencyEntity>, List<CurrencyModel>> {
    override fun mapResponse(input: Map<String, String>): List<CurrencyEntity> {
        val entities = mutableListOf<CurrencyEntity>()
        input.forEach {
            entities.add(CurrencyEntity(currencyCode = it.key, currencyName = it.value))
        }
        return entities.toList()
    }

    override fun mapEntity(input: List<CurrencyEntity>): List<CurrencyModel> {
        return input.map {
            CurrencyModel(currencyCode = it.currencyCode, currencyName = it.currencyName)
        }
    }
}