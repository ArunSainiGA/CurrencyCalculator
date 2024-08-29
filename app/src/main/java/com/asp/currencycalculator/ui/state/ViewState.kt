package com.asp.currencycalculator.ui.state

data class ViewState<T>(
    val data : T? = null,
    val loading: Boolean = false,
    val error: Throwable? = null
)