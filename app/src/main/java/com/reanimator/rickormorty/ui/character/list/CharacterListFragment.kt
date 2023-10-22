package com.reanimator.rickormorty.ui.character.list

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reanimator.rickormorty.ui.base.BaseListFragment
import com.reanimator.rickormorty.ui.character.CharacterAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharacterListFragment : BaseListFragment() {
    val viewModel: CharacterViewModel by activityViewModels()

    private lateinit var adapter: CharacterAdapter

    override fun setupView() {
        adapter = CharacterAdapter {
            val action =
                CharacterListFragmentDirections.actionCharacterListFragmentToCharacterDetailFragment(
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
            adapter.refresh()
            binding.swipeContainer.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.needToRefresh.collect() {
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
}