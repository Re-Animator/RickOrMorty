package com.reanimator.rickormorty.ui.character.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.ui.character.CharacterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CharacterDetailViewModel @AssistedInject constructor(
    private val repository: CharacterRepository,
    @Assisted
    private val characterId: Int
) : ViewModel() {
    private val _character = MutableLiveData<CharacterData>()
    val character: LiveData<CharacterData>
        get() = _character

    private val _episodesFlow = MutableStateFlow<List<Int>>(listOf())
    val episodesFlow: Flow<List<Int>> = _episodesFlow

    val episodesData: Flow<PagingData<EpisodeData>> = episodesFlow
        .flatMapLatest { episodes ->
            repository.getCharacterEpisodesResultStream(episodes)
        }.cachedIn(viewModelScope)

    private var _location: MutableLiveData<LocationData?> = MutableLiveData()
    val location: LiveData<LocationData?>
        get() = _location

    private var _origin: MutableLiveData<LocationData?> = MutableLiveData()
    val origin: LiveData<LocationData?>
        get() = _origin

    init {
        getCharacterById()
        getCharacterEpisodes()
        getLocationByCharacterId()
        getOriginByCharacterId()
    }

    private fun getCharacterEpisodes() {
        viewModelScope.launch {
            _episodesFlow.value = repository.getEpisodesByCharacterId(id = characterId)
        }
    }

    private fun getCharacterById() {
        viewModelScope.launch {
            _character.postValue(repository.getCharacterById(characterId))
        }
    }

    private fun getLocationByCharacterId() {
        viewModelScope.launch {
            _location.postValue(repository.getCharacterLocation(characterId))
        }
    }

    private fun getOriginByCharacterId() {
        viewModelScope.launch {
            _origin.postValue(repository.getCharacterOrigin(characterId))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): CharacterDetailViewModel
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