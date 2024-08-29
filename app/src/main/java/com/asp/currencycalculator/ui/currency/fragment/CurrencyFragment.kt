package com.asp.currencycalculator.ui.currency.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.asp.currencycalculator.R
import com.asp.currencycalculator.databinding.FragmentCurrencyBinding
import com.asp.currencycalculator.ui.currency.adapter.ConversionAdapter
import com.asp.currencycalculator.utils.bindLoading
import com.asp.currencycalculator.utils.textUpdate
import com.asp.currencycalculator.viewmodel.currency.CurrencyViewModel
import com.asp.domain.model.ExchangeModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyFragment : Fragment(R.layout.fragment_currency) {

    @Inject
    lateinit var adapter: ConversionAdapter

    private val viewModel by viewModels<CurrencyViewModel>()

    private var selectedCountryCode: String = ""
    private var amount = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentCurrencyBinding>(view)?.apply {
            setUpRecyclerView(this)
            setTextUpdates(this)

            viewModel.currenciesState.observe(viewLifecycleOwner) { currenciesState ->
                loading.bindLoading(currenciesState)

                currenciesState.data?.let {
                    setUpSpinner(this, it)
                }
            }

            viewModel.exchangeState.observe(viewLifecycleOwner) { state ->
                loading.bindLoading(state)
            }

            viewModel.selectedConversions.observe(viewLifecycleOwner, ::updateExchangeRates)        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setTextUpdates(binding: FragmentCurrencyBinding) {
        binding
            .editText
            .textUpdate()
            .debounce(500)
            .filterNot { it.isNullOrBlank() }
            .distinctUntilChangedBy {
                it.toString()
            }
            .onEach {
                amount = it.toString().toDouble()
                requestCurrencyExchange()
            }
            .launchIn(lifecycleScope)
    }

    private fun setUpRecyclerView(binding: FragmentCurrencyBinding) {
        binding.recyclerView.adapter = adapter
    }

    private fun setUpSpinner(binding: FragmentCurrencyBinding, currencies: Array<String>) {
        with(binding) {
            (spinnerLayout.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(currencies)
            (spinnerLayout.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { adapterView, _, i, _ ->
                selectedCountryCode = adapterView.getItemAtPosition(i).toString()
                requestCurrencyExchange()
            }
        }
    }

    private fun requestCurrencyExchange() {
        Toast.makeText(requireContext(), "Requesting: $selectedCountryCode, $amount", Toast.LENGTH_SHORT).show()
        viewModel.getCurrencyConversions(selectedCountryCode, amount)
    }

    private fun updateExchangeRates(models: List<ExchangeModel>) {
        adapter.updateItems(models)
    }

}