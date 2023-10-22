package com.reanimator.rickormorty.ui.character.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.data.filters.CharacterFilter
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.ui.character.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    val repository: CharacterRepository
) : ViewModel() {
    private val _filterFlow = MutableStateFlow(CharacterFilter())
    val filterFlow: Flow<CharacterFilter> = _filterFlow

    private val _needToRefresh = MutableStateFlow(false)
    val needToRefresh: Flow<Boolean>
        get() = _needToRefresh

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<CharacterData>> = filterFlow
        .flatMapLatest { filter ->
            repository.getCharacterResultStream(filter)
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(searchQuery: String?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateSearchQuery(searchQuery)
    }

    fun setGenderFilter(gender: CharacterFilter.CharacterGender?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateGender(gender)
        _needToRefresh.value = true
        _needToRefresh.value = false
    }

    fun setStatusFilter(status: CharacterFilter.CharacterStatus?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateStatus(status)
        _needToRefresh.value = true
        _needToRefresh.value = false
    }
}