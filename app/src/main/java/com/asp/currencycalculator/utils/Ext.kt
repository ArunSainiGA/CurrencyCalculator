package com.asp.currencycalculator.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.lifecycle.MutableLiveData
import com.asp.currencycalculator.ui.state.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textUpdate(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val watcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                trySend(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        addTextChangedListener(watcher)
        awaitClose {
            removeTextChangedListener(watcher)
        }
    }.onStart {
        emit(text)
    }
}

// Note: Ideally state updates are mutually exclusive and we should create a mapper from our own Result object (from data layer) or we can have Either lib
// to view state to make this behaviour more generic and better but for simplicity creating three different extensions on MutableLiveData
fun <T> MutableLiveData<ViewState<T>>.onLoad() {
    this.value = ViewState(
        loading = true
    )
}

fun <T> MutableLiveData<ViewState<T>>.onSuccess(d: T) {
    this.value = ViewState(
        data = d
    )
}

fun <T> MutableLiveData<ViewState<T>>.onError(e: Throwable) {
    this.value = ViewState(
        error = e
    )
}

fun <T> View.bindLoading(state: ViewState<T>) {
    visibility = if (state.loading)
        View.VISIBLE
    else
        View.GONE
}