package com.asp.data.mapper

import com.asp.data.local.entity.ExchangeEntity
import com.asp.data.remote.response.ExchangeResponse
import com.asp.domain.model.ExchangeModel
import javax.inject.Inject

class ExchangeMapper @Inject constructor(): Mapper<ExchangeResponse, List<ExchangeEntity>, List<ExchangeModel>> {
    override fun mapResponse(input: ExchangeResponse): List<ExchangeEntity> {
        val entities = mutableListOf<ExchangeEntity>()
        input.rates?.forEach {
            entities.add(ExchangeEntity(currencyCode = it.key, rate = it.value))
        }
        return entities.toList()
    }

    override fun mapEntity(input: List<ExchangeEntity>): List<ExchangeModel> {
        return input.map {
            ExchangeModel(currencyCode = it.currencyCode, rate = it.rate)
        }
    }
}