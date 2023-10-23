package com.reanimator.rickormorty.ui.episode.detail

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
import com.reanimator.rickormorty.databinding.FragmentEpisodeDetailBinding
import com.reanimator.rickormorty.ui.character.CharacterAdapter
import com.reanimator.rickormorty.utils.Constants.Companion.ERROR_DOWNLOADING_APPEND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeDetailFragment : Fragment() {
    private val navigationArgs: EpisodeDetailFragmentArgs by navArgs()

    @Inject
    lateinit var factory: EpisodeDetailViewModel.Factory
    private val viewModel: EpisodeDetailViewModel by viewModels {
        EpisodeDetailViewModel.provideViewModelFactory(factory, navigationArgs.episodeId)
    }

    private var _binding: FragmentEpisodeDetailBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var adapter: CharacterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEpisodeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.episode.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.apply {
                    name.text = it.name
                    episode.text = it.episode
                    airDate.text = it.air_date
                }
            }
        }

        adapter = CharacterAdapter {
            findNavController().navigate(
                EpisodeDetailFragmentDirections.actionEpisodeDetailFragmentToCharacterDetailFragment(
                    it.id
                )
            )
        }

        binding.listItem.list.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.charactersData.collectLatest {
                adapter.submitData(it)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}