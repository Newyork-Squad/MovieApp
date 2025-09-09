package com.karrar.movieapp.ui.profile.watchhistory
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.ItemWatchHistoryBinding
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseDiffUtil
import com.karrar.movieapp.ui.base.BaseInteractionListener

class WatchHistoryAdapter(
    private var  items: MutableList<MediaHistoryUiState>,
    listener: WatchHistoryInteractionListener,
) : BaseAdapter<MediaHistoryUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_watch_history

    fun removeItem(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Update adapter data
   fun setItemList(newItems: List<MediaHistoryUiState>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Existing getItem method
    fun getItem(position: Int): MediaHistoryUiState {
        return items[position]
    }

    // Add other required methods like onCreateViewHolder, onBindViewHolder etc.
    class WatchHistoryViewHolder(private val binding: ItemWatchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaHistoryUiState, listener: WatchHistoryInteractionListener) {
            binding.item = item
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}



interface WatchHistoryInteractionListener : BaseInteractionListener {
    fun onClickMovie(item: MediaHistoryUiState)
    fun onDeleteClick(item: MediaHistoryUiState)
}