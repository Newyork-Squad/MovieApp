package com.karrar.movieapp.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.base.BasePagingAdapter
import com.karrar.movieapp.ui.category.uiState.MediaUIState

class CategoryAdapter(listener: MediaInteractionListener) :
    BasePagingAdapter<MediaUIState>(MediaComparator, listener) {

    private var isGridMode: Boolean = true

    fun setGridMode(grid: Boolean) {
        if (isGridMode == grid) return
        isGridMode = grid
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridMode) R.layout.item_category_grid else R.layout.item_category_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            viewType,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder)
            getItem(position)?.let { bind(it, holder) }

    }

    override val layoutID: Int = R.layout.item_category_grid

    object MediaComparator : DiffUtil.ItemCallback<MediaUIState>() {
        override fun areItemsTheSame(oldItem: MediaUIState, newItem: MediaUIState) =
            oldItem.mediaID == newItem.mediaID

        override fun areContentsTheSame(oldItem: MediaUIState, newItem: MediaUIState) =
            oldItem == newItem
    }
}
