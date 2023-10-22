package com.reanimator.rickormorty.ui.location.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.reanimator.rickormorty.R
import com.reanimator.rickormorty.databinding.FragmentListBinding
import com.reanimator.rickormorty.ui.location.LocationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationListFragment : Fragment() {
    private val viewModel: LocationViewModel by activityViewModels()

    private var _binding: FragmentListBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var adapter: LocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LocationAdapter {
            val action =
                LocationListFragmentDirections.actionLocationsListFragmentToLocationDetailFragment(
                    it.id
                )
            this.findNavController().navigate(action)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.filter -> {
                        binding.list.scrollToPosition(0)
                        findNavController().navigate(
                            LocationListFragmentDirections.actionLocationsListFragmentToLocationFilterFragment()
                        )
                        return true
                    }

                    R.id.search_view -> {
                        return true
                    }
                }
                return false
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                (menu.findItem(R.id.search_view).actionView as SearchView)
                    .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (!newText.isNullOrEmpty()) {
                                binding.list.scrollToPosition(0)
                                viewModel.updateSearchQuery(newText)
                            } else {
                                viewModel.updateSearchQuery(null)
                            }
                            adapter.refresh()
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }
                    })
            }
        }, viewLifecycleOwner)

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

        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                binding.prependProgress.isVisible = it.mediator?.prepend is LoadState.Loading
                binding.appendProgress.isVisible = it.mediator?.append is LoadState.Loading

                if (adapter.itemCount < 1) {
                    binding.list.isVisible = it.mediator?.refresh !is LoadState.Error
                    binding.noDataField.isVisible = it.mediator?.refresh is LoadState.Error
                }


            }
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