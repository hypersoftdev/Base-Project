package com.hypersoft.baseproject.helpers.interfaces

import com.hypersoft.baseproject.roomdb.tables.CountryTable

interface OnCountryItemClickListener {
    fun onCountryClick(countryTable: CountryTable)
}