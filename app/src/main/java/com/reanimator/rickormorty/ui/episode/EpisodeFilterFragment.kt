package com.reanimator.rickormorty.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reanimator.rickormorty.R
import com.reanimator.rickormorty.data.filters.EpisodeFilter
import com.reanimator.rickormorty.databinding.FragmentEpisodeFilterBinding
import com.reanimator.rickormorty.ui.episode.list.EpisodeViewModel
import kotlinx.coroutines.launch

class EpisodeFilterFragment : DialogFragment() {
    private val viewModel: EpisodeViewModel by activityViewModels()
    private var _binding: FragmentEpisodeFilterBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEpisodeFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterFlow.collect {
                when (it.season) {
                    EpisodeFilter.SEASON_1 -> binding.seasonOne.isChecked = true
                    EpisodeFilter.SEASON_2 -> binding.seasonTwo.isChecked = true
                    EpisodeFilter.SEASON_3 -> binding.seasonThree.isChecked = true
                    EpisodeFilter.SEASON_4 -> binding.seasonFour.isChecked = true
                    EpisodeFilter.SEASON_5 -> binding.seasonFive.isChecked = true
                }
            }
        }

        binding.applyButton.setOnClickListener {
            val season = when (binding.seasonFilter.checkedChipId) {
                R.id.season_one -> EpisodeFilter.EpisodeSeason.SeasonOne
                R.id.season_two -> EpisodeFilter.EpisodeSeason.SeasonTwo
                R.id.season_three -> EpisodeFilter.EpisodeSeason.SeasonThree
                R.id.season_four -> EpisodeFilter.EpisodeSeason.SeasonFour
                R.id.season_five -> EpisodeFilter.EpisodeSeason.SeasonFive
                else -> null
            }
            viewModel.setFilter(season)
            findNavController().navigateUp()
        }

        binding.cancelButton.setOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}