package com.reanimator.rickormorty.ui.character

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reanimator.rickormorty.R
import com.reanimator.rickormorty.data.filters.CharacterFilter
import com.reanimator.rickormorty.databinding.FragmentCharacterFilterBinding
import com.reanimator.rickormorty.ui.character.list.CharacterViewModel
import kotlinx.coroutines.launch

class CharacterFilterFragment : DialogFragment() {
    private val viewModel: CharacterViewModel by activityViewModels()

    private var _binding: FragmentCharacterFilterBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindFilterStateChange()
        applyButtonSetup()
        bindCancelButtonBehaviour()
    }

    private fun bindCancelButtonBehaviour() {
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindFilterStateChange() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterFlow.collect {
                when (it.gender) {
                    CharacterFilter.GENDER_FEMALE -> binding.genderFemale.isChecked = true
                    CharacterFilter.GENDER_MALE -> binding.genderMale.isChecked = true
                    CharacterFilter.GENDER_GENDERLESS -> binding.genderGenderless.isChecked = true
                    CharacterFilter.GENDER_UNKNOWN -> binding.genderUnknown.isChecked = true
                }
                when (it.status) {
                    CharacterFilter.STATUS_DEAD -> binding.statusDead.isChecked = true
                    CharacterFilter.STATUS_ALIVE -> binding.statusAlive.isChecked = true
                    CharacterFilter.STATUS_UNKNOWN -> binding.statusUnknown.isChecked = true
                }
            }
        }
    }

    private fun applyButtonSetup() {
        binding.applyButton.setOnClickListener {
            val gender = when (binding.genderFilter.checkedChipId) {
                R.id.gender_male -> CharacterFilter.CharacterGender.Male
                R.id.gender_female -> CharacterFilter.CharacterGender.Female
                R.id.gender_genderless -> CharacterFilter.CharacterGender.Genderless
                R.id.gender_unknown -> CharacterFilter.CharacterGender.Unknown
                else -> null
            }
            val status = when (binding.statusFilter.checkedChipId) {
                R.id.status_alive -> CharacterFilter.CharacterStatus.Alive
                R.id.status_dead -> CharacterFilter.CharacterStatus.Dead
                R.id.status_unknown -> CharacterFilter.CharacterStatus.Unknown
                else -> null
            }
            Log.d("DEBUG", gender.toString() + status.toString())
            viewModel.setGenderFilter(gender)
            viewModel.setStatusFilter(status)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}