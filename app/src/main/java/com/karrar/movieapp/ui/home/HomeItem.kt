package com.karrar.movieapp.ui.home

import com.karrar.movieapp.domain.enums.HomeItemsType
import com.karrar.movieapp.ui.home.homeUiState.FeaturedCollectionUiState
import com.karrar.movieapp.ui.home.homeUiState.PopularUiState
import com.karrar.movieapp.ui.models.ActorUiState
import com.karrar.movieapp.ui.models.MediaUiState
import com.karrar.movieapp.ui.myList.myListUIState.CreatedListUIState
import com.karrar.movieapp.ui.profile.watchhistory.MediaHistoryUiState

sealed class HomeItem(val priority: Int) {

    data class Slider(val items: List<PopularUiState>) : HomeItem(0)

    data class RecentlyReleased(
        val items: List<MediaUiState>,
        val type: HomeItemsType = HomeItemsType.RECENTLY_RELEASED
    ) : HomeItem(1)

    object WhatShouldWatch:HomeItem(2)

    data class Upcoming(
        val items: List<MediaUiState>,
        val type: HomeItemsType = HomeItemsType.UPCOMING
    ) : HomeItem(3)

    data class TopRatedTvShows(
        val items: List<MediaUiState>,
        val type: HomeItemsType = HomeItemsType.TOP_RATED_TV_SHOWS
    ) : HomeItem(6)

    data class RecentlyViewed(
        val items: List<MediaHistoryUiState>,
        val type: HomeItemsType = HomeItemsType.RECENTLY_VIEWED
    ) : HomeItem(7)

    data class Collections(
        val items: List<CreatedListUIState>,
        val type: HomeItemsType = HomeItemsType.COLLECTIONS
    ) : HomeItem(8)

    data class Actor(val items: List<ActorUiState>) : HomeItem(9)

  //  data class RecentlyViewed(val items: List<MediaHistoryUiState>, val type: HomeItemsType = HomeItemsType.RECENTLY_VIEWED) : HomeItem(10)

    data class FeaturedCollections(
        val items: List<FeaturedCollectionUiState>,
        val type: HomeItemsType = HomeItemsType.FEATURED_COLLECTIONS
    ) : HomeItem(5)
    object NeedMoreToWatch:HomeItem(9)
}