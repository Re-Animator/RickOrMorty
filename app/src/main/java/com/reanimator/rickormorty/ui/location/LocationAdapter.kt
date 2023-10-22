package com.reanimator.rickormorty.ui.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reanimator.rickormorty.databinding.LocationListItemBinding
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.ui.base.BaseAdapter

class LocationAdapter(
    private val onItemClicked: (LocationData) -> Unit
) : BaseAdapter<LocationData, LocationAdapter.LocationsViewHolder>(
    onItemClicked,
    DiffCallback
) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LocationsViewHolder {
        return LocationsViewHolder(
            LocationListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun bindViewHolder(holder: LocationsViewHolder, item: LocationData) {
        holder.bind(item)
    }

    class LocationsViewHolder(private val binding: LocationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LocationData) {
            binding.apply {
                name.text = data.name
                type.text = data.type
                dimension.text = data.dimension
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LocationData>() {
            override fun areItemsTheSame(oldItem: LocationData, newItem: LocationData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LocationData, newItem: LocationData): Boolean =
                oldItem == newItem
        }
    }
}