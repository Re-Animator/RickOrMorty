package com.reanimator.rickormorty.observer

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    fun isNetworkAvailable(): Boolean

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}