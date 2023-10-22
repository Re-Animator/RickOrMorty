package com.reanimator.rickormorty.ui.location.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.data.filters.LocationFilter
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.ui.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _filterFlow = MutableStateFlow(LocationFilter())
    val filterFlow: Flow<LocationFilter> = _filterFlow

    val data: Flow<PagingData<LocationData>> = filterFlow
        .flatMapLatest { filter ->
            repository.getLocationResultStream(filter)
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(searchQuery: String?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateSearchQuery(searchQuery)
    }

    fun updateTypeSearchQuery(typeSearchQuery: String?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateTypeSearchQuery(typeSearchQuery)
    }
}