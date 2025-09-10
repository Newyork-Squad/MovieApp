package com.karrar.movieapp.ui.profile.watchhistory
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.ItemWatchHistoryBinding
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseDiffUtil
import com.karrar.movieapp.ui.base.BaseInteractionListener
import kotlinx.coroutines.flow.MutableStateFlow

class WatchHistoryAdapter(
    private var  items: MutableList<MediaHistoryUiState>,
    listener: WatchHistoryInteractionListener,
) : BaseAdapter<MediaHistoryUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_watch_history
    private val deleteButtonVisibility = MutableStateFlow(false)


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            bind(holder, position)
            val buttonVisibility = if (deleteButtonVisibility.value) View.VISIBLE else View.GONE
            holder.binding.root.findViewById<ImageButton>(R.id.action_button).visibility = buttonVisibility


        }
    }

    fun setDeleteButtonVisibility(isVisible: Boolean) {
        deleteButtonVisibility.value = isVisible
    }


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

}

interface WatchHistoryListener:BaseInteractionListener{
    fun onClickToExploreScreen()
    fun onDeleteClick(item: MediaHistoryUiState)
}