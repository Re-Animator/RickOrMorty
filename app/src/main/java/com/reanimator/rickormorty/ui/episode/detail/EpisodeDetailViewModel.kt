package com.reanimator.rickormorty.ui.episode.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.ui.episode.EpisodeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EpisodeDetailViewModel @AssistedInject constructor(
    private val repository: EpisodeRepository,
    @Assisted
    private val episodeId: Int
) : ViewModel() {
    private val _episode = MutableLiveData<EpisodeData>()
    val episode: LiveData<EpisodeData>
        get() = _episode

    private val _characterFlow = MutableStateFlow<List<Int>>(listOf(1, 2))
    val characterFlow: Flow<List<Int>> = _characterFlow

    val charactersData: Flow<PagingData<CharacterData>> = characterFlow
        .flatMapLatest { characters ->
            repository.getEpisodeCharactersResultStream(characters)
        }
        .cachedIn(viewModelScope)

    init {
        getEpisodeById()
        getEpisodeCharacters()
    }

    private fun getEpisodeById() {
        viewModelScope.launch {
            _episode.postValue(repository.getEpisodeById(episodeId))
        }
    }

    private fun getEpisodeCharacters() {
        viewModelScope.launch {
            _characterFlow.value = repository.getCharactersByEpisodeId(episodeId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): EpisodeDetailViewModel
    }

    companion object {
        fun provideViewModelFactory(factory: Factory, id: Int): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(id) as T
                }
            }
        }
    }
}