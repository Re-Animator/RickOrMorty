package com.reanimator.rickormorty.ui.episode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reanimator.rickormorty.databinding.EpisodeListItemBinding
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.ui.base.BaseAdapter

class EpisodeAdapter(
    private val onItemClicked: (EpisodeData) -> Unit
) : BaseAdapter<EpisodeData, EpisodeAdapter.EpisodesViewHolder>(
    onItemClicked,
    DiffCallback
) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EpisodesViewHolder {
        return EpisodesViewHolder(
            EpisodeListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun bindViewHolder(holder: EpisodesViewHolder, item: EpisodeData) {
        holder.bind(item)
    }

    class EpisodesViewHolder(private val binding: EpisodeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EpisodeData) {
            binding.apply {
                name.text = data.name
                number.text = data.episode
                airDate.text = data.air_date
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<EpisodeData>() {
            override fun areItemsTheSame(oldItem: EpisodeData, newItem: EpisodeData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: EpisodeData, newItem: EpisodeData): Boolean =
                oldItem == newItem
        }
    }
}