package com.reanimator.rickormorty.ui.base

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val onItemClicked: (T) -> Unit,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffCallback) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        val current = getItem(position)
        if (current != null) {
            holder.itemView.setOnClickListener {
                onItemClicked(current)
            }
            bindViewHolder(holder, current)
        }
    }

    open fun bindViewHolder(holder: VH, item: T) {}
}