package com.karrar.movieapp.ui.search.adapters

import com.karrar.movieapp.ui.base.*
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState


class SearchSuggestedAdapter(
    items: List<SearchKeywordUIState>,
    val layout: Int, listener: SearchSuggestedInteractionListener
) : BaseAdapter<SearchKeywordUIState>(items,listener){
    override val layoutID: Int = layout
}


interface SearchSuggestedInteractionListener : BaseInteractionListener {
    fun onClickSearchSuggested(name: String)
}