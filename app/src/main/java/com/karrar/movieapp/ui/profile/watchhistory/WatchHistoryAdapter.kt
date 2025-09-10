package com.karrar.movieapp.ui.profile.watchhistory
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener

class WatchHistoryAdapter(
    private var  items: List<MediaHistoryUiState>,
    listener: WatchHistoryInteractionListener,
) : BaseAdapter<MediaHistoryUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_watch_history


}



interface WatchHistoryInteractionListener : BaseInteractionListener {
    fun onClickMovie(item: MediaHistoryUiState)

}

interface WatchHistoryListener:BaseInteractionListener{
    fun onDeleteClick(item: MediaHistoryUiState)
}