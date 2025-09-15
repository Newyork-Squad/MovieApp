package com.karrar.movieapp.ui.search.adapters

import com.karrar.movieapp.ui.base.*
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState


class SearchHistoryAdapter(items: List<SearchHistoryUIState>,val layout: Int, listener: SearchHistoryInteractionListener)
    : BaseAdapter<SearchHistoryUIState>(items,listener){
    override val layoutID: Int = layout
}

interface SearchHistoryInteractionListener : BaseInteractionListener {
    fun onClickSearchHistory(name: String)
}