package com.karrar.movieapp.ui.home.homeUiState

import androidx.annotation.StringRes
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.home.HomeItem

data class HomeUiState (
    val username : String = "",
    val isLoggedIn: Boolean = false,
    val popularMovies: HomeItem = HomeItem.Slider(emptyList()),
    val upcomingMovies: HomeItem = HomeItem.Upcoming(emptyList()),
    val topRatedSeries: HomeItem = HomeItem.TopRatedTvShows(emptyList()),
    val recentlyReleasedSeries: HomeItem = HomeItem.RecentlyReleased(emptyList()),
    val recentlyViewed: HomeItem = HomeItem.RecentlyViewed(emptyList()),
    val collections: HomeItem = HomeItem.Collections(emptyList()),
    val whatShouldIWatch:HomeItem=HomeItem.WhatShouldWatch,
    val needMoreToWatch:HomeItem=HomeItem.NeedMoreToWatch,
    val featured: HomeItem = HomeItem.FeaturedCollections(emptyList()),
    val featuredCollections: List<FeaturedCollectionUiState> = emptyList(),
    val matchedItems: HomeItem = HomeItem.MatchedItems(emptyList()),
    val isLoading:Boolean = false,
    val error : List<String> = emptyList(),
)

data class FeaturedCollectionUiState(
    @StringRes val title: Int,
    val imageResId: Int,
    val target: FeaturedCollectionsTarget
)

enum class FeaturedCollectionsTarget(@StringRes val title: Int, val id: Int) {
    LATE_NIGHT_THRILLS(R.string.late_night_thrills,53),
    MIND_BENDING_STORIES(R.string.mind_bending_stories,9648),
    CINEMATIC_MASTERPIECES(R.string.cinematic_masterpieces,18),
    FAMILY_NIGHT_PICKS(R.string.family_night_picks,35),
    BASED_ON_TRUE_EVENTS(R.string.based_on_true_events,36),
    FEEL_GOOD_FAVORITES(R.string.feel_good_favorites,35),
}