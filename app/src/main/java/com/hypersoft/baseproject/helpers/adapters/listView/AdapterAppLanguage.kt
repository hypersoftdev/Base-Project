package com.hypersoft.baseproject.helpers.adapters.listView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.helpers.models.LanguageItem
import com.hypersoft.baseproject.databinding.ItemSpinnerLanguageBinding

class AdapterLanguage(context: Context, languages: List<LanguageItem>) : ArrayAdapter<LanguageItem>(context, 0, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {
        val item = getItem(position)
        val binding = DataBindingUtil.inflate<ItemSpinnerLanguageBinding>(LayoutInflater.from(context), R.layout.item_spinner_language, parent, false)
        binding.item = item
        return binding.root
    }
}