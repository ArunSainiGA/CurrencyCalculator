package com.asp.currencycalculator.viewmodel.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asp.currencycalculator.ui.state.ViewState
import com.asp.currencycalculator.utils.AppConstant
import com.asp.currencycalculator.utils.onError
import com.asp.currencycalculator.utils.onLoad
import com.asp.currencycalculator.utils.onSuccess
import com.asp.domain.model.ExchangeModel
import com.asp.domain.usecase.GetCurrencyUseCase
import com.asp.domain.usecase.GetExchangeRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val getExchangeUseCase: GetExchangeRateUseCase
): ViewModel() {

    private val _currenciesState = MutableLiveData<ViewState<Array<String>>>()
    val currenciesState: LiveData<ViewState<Array<String>>> = _currenciesState

    private val _exchangeState = MutableLiveData<ViewState<List<ExchangeModel>>>()
    val exchangeState: LiveData<ViewState<List<ExchangeModel>>> = _exchangeState

    init {
        getCurrencyList()
    }

    // Note: Instead of calling APIs directly, we can have Flow EventBus created to make it more reactive
    @VisibleForTesting
    fun getCurrencyList() {
        viewModelScope.launch {
            getCurrencyUseCase().onStart {
                _currenciesState.onLoad()
            }.collect { result ->
                result.fold({
                    _currenciesState.onSuccess(
                        it.map {
                            model -> model.currencyCode
                        }.toTypedArray()
                    )
                }, {
                    _currenciesState.onError(it)
                })
            }
        }
    }

    val selectedConversions = MutableLiveData<List<ExchangeModel>>()

    // Note: Instead of calling APIs directly, we can have Flow EventBus created to make it more reactive
    fun getCurrencyConversions(currencyCode: String, amount: Double) {
        if (amount != 0.0 && currencyCode.isNotBlank()) {
            viewModelScope.launch {
                getExchangeUseCase.invoke(AppConstant.APP_ID, false).onStart {
                    _exchangeState.onLoad()
                }.collect { result ->
                    result.fold({
                        _exchangeState.onSuccess(it)
                        updateSelectedConversion(currencyCode, amount, it)
                    }, {
                        _exchangeState.onError(it)
                    })
                }
            }
        }
    }

    private fun updateSelectedConversion(currencyCode: String, amount: Double, rates: List<ExchangeModel>) {
        rates.find { it.currencyCode == currencyCode }?.rate?.let { selectedRate ->
            selectedConversions.value = rates.map {
                ExchangeModel(it.currencyCode, it.rate.div(selectedRate ?: 1.0) * amount)
            }
        }
    }

}