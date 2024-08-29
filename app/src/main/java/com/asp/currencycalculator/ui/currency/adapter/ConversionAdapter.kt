package com.asp.currencycalculator.ui.currency.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.asp.currencycalculator.R
import com.asp.currencycalculator.databinding.ViewItemConversionBinding
import com.asp.domain.model.ExchangeModel
import javax.inject.Inject

class ConversionAdapter @Inject constructor(): RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder>() {

    private var items: List<ExchangeModel>? = null

    class ConversionViewHolder(private val binding: ViewItemConversionBinding): ViewHolder(binding.root) {
        fun bind(model: ExchangeModel) {
            binding.model = model
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_item_conversion, parent, false))
    }

    override fun getItemCount() = items?.count() ?: 0

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        items?.let {
            holder.bind(it[position])
        }
    }

    fun updateItems(items: List<ExchangeModel>) {
        this.items = items
        notifyDataSetChanged()
    }

}