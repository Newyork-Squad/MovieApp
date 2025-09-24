package com.karrar.movieapp.ui.home.homeUiState

import com.karrar.movieapp.ui.home.HomeItem

data class HomeUiState (
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
    val title: String,
    val imageResId: Int,
    val target: FeaturedCollectionsTarget
)

enum class FeaturedCollectionsTarget {
    LATE_NIGHT_THRILLS,
    MIND_BENDING_STORIES,
    CINEMATIC_MASTERPIECES,
    FAMILY_NIGHT_PICKS,
    BASED_ON_TRUE_EVENTS,
    FEEL_GOOD_FAVORITES
}