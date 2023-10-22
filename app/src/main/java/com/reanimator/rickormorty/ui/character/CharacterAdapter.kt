package com.reanimator.rickormorty.ui.character

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.reanimator.rickormorty.databinding.CharacterListItemBinding
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.ui.base.BaseAdapter

class CharacterAdapter(
    private val onItemClicked: (CharacterData) -> Unit
) : BaseAdapter<CharacterData, CharacterAdapter.CharactersViewHolder>(
    onItemClicked,
    DiffCallback
) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CharactersViewHolder {
        return CharactersViewHolder(
            CharacterListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun bindViewHolder(holder: CharactersViewHolder, item: CharacterData) {
        holder.bind(item)
    }

    class CharactersViewHolder(private val binding: CharacterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CharacterData) {
            binding.apply {
                image.load(data.image)
                name.text = data.name
                species.text = data.species
                gender.text = data.gender
                status.text = data.status
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CharacterData>() {
            override fun areItemsTheSame(oldItem: CharacterData, newItem: CharacterData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CharacterData,
                newItem: CharacterData
            ): Boolean =
                oldItem == newItem
        }

    }
}