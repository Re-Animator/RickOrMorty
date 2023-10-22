package com.reanimator.rickormorty.ui.location.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.ui.location.LocationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LocationDetailViewModel @AssistedInject constructor(
    private val repository: LocationRepository,
    @Assisted
    private val locationId: Int
) : ViewModel() {
    private val _location = MutableLiveData<LocationData>()
    val location: LiveData<LocationData>
        get() = _location

    private val _characterFlow = MutableStateFlow<List<Int>?>(null)
    val characterFlow: Flow<List<Int>?> = _characterFlow

    val charactersData: Flow<PagingData<CharacterData>> = characterFlow
        .flatMapLatest { characters ->
            repository.getLocationCharactersResultStream(characters)
        }.filterNotNull()
        .cachedIn(viewModelScope)

    init {
        getLocationById()
        getLocationCharacters()
    }

    private fun getLocationById() {
        viewModelScope.launch {
            _location.postValue(repository.getLocationById(locationId))
        }
    }

    private fun getLocationCharacters() {
        viewModelScope.launch {
            _characterFlow.value = repository.getCharactersByEpisodeId(locationId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): LocationDetailViewModel
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