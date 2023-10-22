package com.reanimator.rickormorty.ui.episode.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.reanimator.rickormorty.ui.base.BaseListFragment
import com.reanimator.rickormorty.ui.episode.EpisodeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EpisodeListFragment : BaseListFragment() {
    private val viewModel: EpisodeViewModel by activityViewModels()

    private lateinit var adapter: EpisodeAdapter

     override fun setupView() {
        adapter = EpisodeAdapter {
            val action =
                EpisodeListFragmentDirections.actionEpisodesListFragmentToEpisodeDetailFragment(
                    it.id
                )
            this.findNavController().navigate(action)
        }

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            onRefreshSwipe()
            binding.swipeContainer.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.needToRefresh.collect {
                if (it) {
                    binding.list.scrollToPosition(0)
                    adapter.refresh()
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                setTopBarLoadingState(it.mediator?.prepend)
                setBottomBarLoadingState(it.mediator?.append)
                noDataState(it.mediator?.refresh, adapter.itemCount)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                appendLoadFailed(it?.mediator?.append)
            }
        }
    }

    protected override fun setBottomBarLoadingState(loadState: LoadState?) {
        binding.appendProgress.isVisible = loadState is LoadState.Loading
    }

    protected override fun noDataState(loadState: LoadState?, itemCount: Int) {
        if (itemCount < 1) {
            binding.list.isVisible = loadState !is LoadState.Error
            binding.noDataField.isVisible = loadState is LoadState.Error
        }
    }

    override fun onSearchQueryTextChanged(newText: String?) {
        if (!newText.isNullOrEmpty()) {
            binding.list.scrollToPosition(0)
            viewModel.updateSearchQuery(newText)
        } else {
            viewModel.updateSearchQuery(null)
        }
        adapter.refresh()
    }

    override fun onRefreshSwipe() {
        adapter.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EpisodeAdapter {
            val action =
                EpisodeListFragmentDirections.actionEpisodesListFragmentToEpisodeDetailFragment(
                    it.id
                )
            this.findNavController().navigate(action)
        }

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            onRefreshSwipe()
            binding.swipeContainer.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.needToRefresh.collect {
                if (it) {
                    binding.list.scrollToPosition(0)
                    adapter.refresh()
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                setTopBarLoadingState(it.mediator?.prepend)
                setBottomBarLoadingState(it.mediator?.append)
                noDataState(it.mediator?.refresh, adapter.itemCount)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                appendLoadFailed(it.mediator?.append)
            }
        }
    }
}