package com.hypersoft.baseproject.roomdb.repository

import androidx.annotation.WorkerThread
import com.hypersoft.baseproject.roomdb.daos.GeneralProjectDao
import com.hypersoft.baseproject.roomdb.tables.CountryTable
import kotlinx.coroutines.flow.Flow

class GeneralProjectRepository(private val generalProjectDao: GeneralProjectDao) {

    /***
     * country_table methods
     */

    val allCountryList: Flow<List<CountryTable>> = generalProjectDao.getAllCountryList()

    @WorkerThread
    suspend fun insertCountry(countryTable: CountryTable) {
        generalProjectDao.insertCountry(countryTable)
    }

    @WorkerThread
    suspend fun deleteCountry(countryTable: CountryTable) {
        generalProjectDao.deleteCountry(countryTable)
    }

    @WorkerThread
    suspend fun updateCountry(countryTable: CountryTable) {
        generalProjectDao.updateCountry(countryTable)
    }

    @WorkerThread
    suspend fun deleteAllCountryList() {
        generalProjectDao.deleteAllCountry()
    }
}