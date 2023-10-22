package com.reanimator.rickormorty.ui.episode.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.data.filters.EpisodeFilter
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.ui.episode.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : ViewModel() {
    private val _filterFlow = MutableStateFlow(EpisodeFilter())
    val filterFlow: Flow<EpisodeFilter> = _filterFlow

    private val _needToRefresh = MutableStateFlow(false)
    val needToRefresh: Flow<Boolean>
        get() = _needToRefresh

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<EpisodeData>> = filterFlow
        .flatMapLatest { filter ->
            repository.getEpisodeResultStream(filter)
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(searchQuery: String?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateSearchQuery(searchQuery)
    }

    fun setFilter(season: EpisodeFilter.EpisodeSeason?) {
        val currentFilter = _filterFlow.value
        _filterFlow.value = currentFilter.updateSeasonFilter(season)
        _needToRefresh.value = true
        _needToRefresh.value = false
    }
}