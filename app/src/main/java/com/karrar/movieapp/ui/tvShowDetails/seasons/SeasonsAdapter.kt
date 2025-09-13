package com.karrar.movieapp.ui.tvShowDetails.seasons

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.movieDetails.DetailInteractionListener
import com.karrar.movieapp.ui.tvShowDetails.tvShowUIState.SeasonUIState

class SeasonsAdapter(
    items: List<SeasonUIState>,
    listener: DetailInteractionListener
) : BaseAdapter<SeasonUIState>(items, listener) {
    override val layoutID: Int = R.layout.item_season
}