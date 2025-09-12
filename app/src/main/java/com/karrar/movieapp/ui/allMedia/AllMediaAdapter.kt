package com.karrar.movieapp.ui.allMedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.base.BasePagingAdapter
import com.karrar.movieapp.ui.models.MediaUiState

class AllMediaAdapter(listener: MediaInteractionListener) :
    BasePagingAdapter<MediaUiState>(MediaComparator, listener) {

    private var isGridMode: Boolean = true

    fun setGridMode(grid: Boolean) {
        if (isGridMode == grid) return
        isGridMode = grid
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridMode) R.layout.item_media_see_all else R.layout.item_media_see_all_horizontal
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

    override val layoutID: Int = R.layout.item_media

    object MediaComparator : DiffUtil.ItemCallback<MediaUiState>() {
        override fun areItemsTheSame(oldItem: MediaUiState, newItem: MediaUiState) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MediaUiState, newItem: MediaUiState) =
            oldItem == newItem
    }
}
