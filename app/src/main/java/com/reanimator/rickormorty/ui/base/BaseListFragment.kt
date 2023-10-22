package com.reanimator.rickormorty.ui.base

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
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.reanimator.rickormorty.R
import com.reanimator.rickormorty.databinding.FragmentListBinding
import com.reanimator.rickormorty.ui.character.list.CharacterListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

private const val APPEND_LOAD_ERROR_MESSAGE =
    "Error occurred while downloading, check internet connection"

@AndroidEntryPoint
abstract class BaseListFragment() : Fragment() {
    private var _binding: FragmentListBinding? = null
    val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    abstract fun setupView()

    abstract fun onSearchQueryTextChanged(newText: String?)

    abstract fun onRefreshSwipe()

    protected fun setTopBarLoadingState(loadState: LoadState?) {
        binding.prependProgress.isVisible = loadState is LoadState.Loading
    }

    protected open fun setBottomBarLoadingState(loadState: LoadState?) {
        binding.appendProgress.isVisible = loadState is LoadState.Loading
    }

    protected open fun noDataState(loadState: LoadState?, itemCount: Int) {
        if (itemCount < 1) {
            binding.list.isVisible = loadState !is LoadState.Error
            binding.noDataField.isVisible = loadState is LoadState.Error
        }
    }

    protected fun appendLoadFailed(loadState: LoadState?) {
        if (loadState is LoadState.Error) Toast.makeText(
            requireContext(),
            APPEND_LOAD_ERROR_MESSAGE,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                            CharacterListFragmentDirections.actionCharacterListFragmentToCharacterFilterFragment()
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
                            onSearchQueryTextChanged(newText)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }
                    })
            }
        }, viewLifecycleOwner)


        binding.swipeContainer.setOnRefreshListener {
            onRefreshSwipe()
            binding.swipeContainer.isRefreshing = false
        }

        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}