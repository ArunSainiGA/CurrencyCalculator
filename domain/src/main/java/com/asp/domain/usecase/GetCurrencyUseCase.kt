package com.asp.domain.usecase

import com.asp.domain.model.CurrencyModel
import com.asp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<Result<List<CurrencyModel>>> = repository.getCurrencies()
}