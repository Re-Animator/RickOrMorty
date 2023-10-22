package com.reanimator.rickormorty

import androidx.paging.PagingData
import com.reanimator.rickormorty.data.filters.Filterable
import kotlinx.coroutines.flow.Flow

interface MyRepository {
    suspend fun <T : Any, F : Filterable?> getDataResultStream(filter: F?): Flow<PagingData<T>>
}