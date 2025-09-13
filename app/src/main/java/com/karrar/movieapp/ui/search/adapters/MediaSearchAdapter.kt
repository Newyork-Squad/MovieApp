package com.karrar.movieapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.*
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState


class MediaSearchAdapter(listener: MediaSearchInteractionListener)
    : BasePagingAdapter<MediaUIState>(MediaSearchComparator, listener){

    private var isGridMode: Boolean = true

    fun setGridMode(grid: Boolean) {
        if (isGridMode == grid) return
        isGridMode = grid
        notifyDataSetChanged()
    }
    override fun getItemViewType(position: Int): Int {
        return if (isGridMode) R.layout.item_media_search_grid else R.layout.item_media_search_list
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

    override val layoutID: Int = R.layout.item_media_search_grid

    object MediaSearchComparator : DiffUtil.ItemCallback<MediaUIState>(){
        override fun areItemsTheSame(oldItem: MediaUIState, newItem: MediaUIState) =
            oldItem.mediaID == newItem.mediaID

        override fun areContentsTheSame(oldItem: MediaUIState, newItem: MediaUIState) =
            oldItem == newItem
    }
}

interface MediaSearchInteractionListener : BaseInteractionListener {
    fun onClickMediaResult(media: MediaUIState)
}