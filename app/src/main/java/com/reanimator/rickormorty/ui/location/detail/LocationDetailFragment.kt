package com.reanimator.rickormorty.ui.location.detail

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
import com.reanimator.rickormorty.databinding.FragmentLocationDetailBinding
import com.reanimator.rickormorty.ui.character.CharacterAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationDetailFragment : Fragment() {
    private val navigationArgs: LocationDetailFragmentArgs by navArgs()

    @Inject
    lateinit var factory: LocationDetailViewModel.Factory
    private val viewModel: LocationDetailViewModel by viewModels {
        LocationDetailViewModel.provideViewModelFactory(factory, navigationArgs.locationId)
    }

    private var _binding: FragmentLocationDetailBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var adapter: CharacterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.location.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.apply {
                    name.text = it.name
                    type.text = it.type
                    dimension.text = it.dimension
                }
            }
        }

        adapter = CharacterAdapter {
            findNavController().navigate(
                LocationDetailFragmentDirections.actionLocationDetailFragmentToCharacterDetailFragment(
                    it.id
                )
            )
        }

        val listLayout = binding.listItem
        listLayout.list.adapter = adapter

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
                        "Error occurred while downloading, check internet connection",
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