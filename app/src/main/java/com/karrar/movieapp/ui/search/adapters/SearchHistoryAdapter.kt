package com.karrar.movieapp.ui.search.adapters

import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState


class SearchHistoryAdapter(items: List<SearchHistoryUIState>,val layout: Int, listener: SearchHistoryInteractionListener)
    : BaseAdapter<SearchHistoryUIState>(items,listener){
    override val layoutID: Int = layout
}


interface SearchHistoryInteractionListener : BaseInteractionListener {
    fun onClickSearchHistory(name: String)
    fun onClearAllHistoryClicked()
    fun deleteHistoryItem(item: SearchHistoryUIState)


}