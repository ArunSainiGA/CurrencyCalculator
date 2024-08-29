package com.asp.domain.usecase

import com.asp.domain.model.ExchangeModel
import com.asp.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(appId: String, shouldForceRefresh: Boolean = false): Flow<Result<List<ExchangeModel>>> = repository.getExchangeRates(appId, shouldForceRefresh)
}