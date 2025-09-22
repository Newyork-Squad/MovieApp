package com.karrar.movieapp.ui.home.homeUiState

import androidx.annotation.StringRes
import com.karrar.movieapp.R

enum class HomeItemsType(@StringRes val value :Int) {
    TOP_RATED_TV_SHOWS(R.string.title_top_rated_tv_show),
    RECENTLY_RELEASED(R.string.title_recently_released),
    UPCOMING(R.string.title_upcoming),
    RECENTLY_VIEWED(R.string.title_recently_viewed),
    COLLECTIONS(R.string.title_your_collections),
    NON(-1)
}