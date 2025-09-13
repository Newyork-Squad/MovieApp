package com.karrar.movieapp.ui.reviews

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.movieDetails.movieDetailsUIState.ReviewUIState

class ReviewAdapter(items: List<ReviewUIState>, listener: BaseInteractionListener) :
    BaseAdapter<ReviewUIState>(items, listener) {
    override val layoutID: Int = R.layout.item_top_review
}

