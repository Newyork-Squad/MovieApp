package com.karrar.movieapp.ui.search.adapters

import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState

class RecentViewAdapter(
    items: List<RecentMovieViewedUiState>,
    val layout: Int,
    listener: RecentViewedInteractionListener
) : BaseAdapter<RecentMovieViewedUiState>(items, listener) {
    override val layoutID: Int = layout
}

interface RecentViewedInteractionListener : BaseInteractionListener {
    fun onClickRecentViewed(item: RecentMovieViewedUiState)
}

