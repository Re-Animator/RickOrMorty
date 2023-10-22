package com.reanimator.rickormorty.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.reanimator.rickormorty.databinding.FragmentLocationFilterBinding
import com.reanimator.rickormorty.ui.location.list.LocationViewModel

class LocationFilterFragment : DialogFragment() {
    private val viewModel: LocationViewModel by activityViewModels()

    private var _binding: FragmentLocationFilterBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindBackButtonBehaviour()
        bindSearchView()
    }

    private fun bindSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(searchedText: String?): Boolean {
                if (!searchedText.isNullOrEmpty()) {
                    viewModel.updateSearchQuery(searchedText)
                } else {
                    viewModel.updateSearchQuery(null)
                }
                return true
            }

        })
    }

    private fun bindBackButtonBehaviour() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}