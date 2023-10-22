package com.reanimator.rickormorty.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reanimator.rickormorty.ui.character.CharacterRepository
import com.reanimator.rickormorty.ui.character.detail.CharacterDetailViewModel
import com.reanimator.rickormorty.ui.episode.EpisodeRepository
import com.reanimator.rickormorty.ui.episode.detail.EpisodeDetailViewModel
import com.reanimator.rickormorty.ui.location.LocationRepository
import com.reanimator.rickormorty.ui.location.detail.LocationDetailViewModel

class DetailViewModelFactory(
    private val id: Int,
    private val characterRepository: CharacterRepository,
    private val episodeRepository: EpisodeRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CharacterDetailViewModel::class.java) -> {
                CharacterDetailViewModel(characterRepository, id) as T
            }

            modelClass.isAssignableFrom(EpisodeDetailViewModel::class.java) -> {
                EpisodeDetailViewModel(episodeRepository, id) as T
            }

            modelClass.isAssignableFrom(LocationDetailViewModel::class.java) -> {
                LocationDetailViewModel(locationRepository, id) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}