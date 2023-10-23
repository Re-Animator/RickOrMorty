package com.reanimator.rickormorty.ui.character.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.reanimator.rickormorty.databinding.FragmentCharacterDetailBinding
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.ui.episode.EpisodeAdapter
import com.reanimator.rickormorty.utils.Constants.Companion.ERROR_DOWNLOADING_APPEND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NO_LOCATION_MESSAGE = "There is no such location"
private const val LOCATION_DEFAULT = "Unknown"

@AndroidEntryPoint
class CharacterDetailFragment : Fragment() {
    private val navigationArgs: CharacterDetailFragmentArgs by navArgs()

    @Inject
    lateinit var factory: CharacterDetailViewModel.Factory
    private val viewModel: CharacterDetailViewModel by viewModels {
        CharacterDetailViewModel.provideViewModelFactory(factory, navigationArgs.characterId)
    }

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.apply {
                    name.text = it.name
                    image.load(it.image)
                    species.text = it.species
                    gender.text = it.gender
                }
            }
        }
        viewModel.location.observe(viewLifecycleOwner) {
            binding.location.text = it?.name ?: LOCATION_DEFAULT
        }
        viewModel.origin.observe(viewLifecycleOwner) {
            binding.origin.text = it?.name ?: LOCATION_DEFAULT
        }

        val adapter = EpisodeAdapter {
            findNavController().navigate(
                CharacterDetailFragmentDirections.actionCharacterDetailFragmentToEpisodeDetailFragment(
                    it.id
                )
            )
        }

        binding.listItem.list.adapter = adapter
        binding.listItem.list.layoutManager = LinearLayoutManager(this.requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.episodesData.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.locationInfo.setOnClickListener {
            navigateToLocation(viewModel.location.value)
        }

        binding.originInfo.setOnClickListener {
            navigateToLocation(viewModel.origin.value)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                binding.listItem.prependProgress.isVisible =
                    it.mediator?.prepend is LoadState.Loading
                binding.listItem.appendProgress.isVisible = it.mediator?.append is LoadState.Loading

                if (adapter.itemCount < 1) {
                    binding.listItem.list.isVisible = it.mediator?.refresh !is LoadState.Error
                    binding.listItem.noDataField.isVisible = it.mediator?.refresh is LoadState.Error
                }
            }
        }
        binding.listItem.swipeContainer.setOnRefreshListener {
            adapter.refresh()
            binding.listItem.swipeContainer.isRefreshing = false
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                if (it.mediator?.append is LoadState.Error) {
                    Toast.makeText(
                        requireContext(),
                        ERROR_DOWNLOADING_APPEND,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateToLocation(location: LocationData?) {
        if (location != null) {
            findNavController().navigate(
                CharacterDetailFragmentDirections.actionCharacterDetailFragmentToLocationDetailFragment(
                    location.id
                )
            )
        } else {
            Toast.makeText(
                requireContext(),
                NO_LOCATION_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}