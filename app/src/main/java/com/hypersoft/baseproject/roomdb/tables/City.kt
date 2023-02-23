package com.hypersoft.baseproject.roomdb.tables

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    var cityName: String,
    var cityPostalCode: String
) : Parcelable