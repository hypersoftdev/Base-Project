package com.hypersoft.baseproject.data.repositories.entrance

interface EntranceRepository {
    suspend fun isFirstTime(): Boolean
}