package com.karrar.movieapp.ui.adapters

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.models.MediaUiState


class SimilarTvShowsAdapter(items: List<MediaUiState>, val listener: SimilarTvShowsInteractionListener) :
    BaseAdapter<MediaUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_similar_tv_show
}

interface SimilarTvShowsInteractionListener : BaseInteractionListener {
    fun onClickTvShow(tvShowId: Int)
}