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
    val matchedItems: HomeItem = HomeItem.MatchedItems(emptyList()),
    val isLoading:Boolean = false,
    val error : List<String> = emptyList(),
)