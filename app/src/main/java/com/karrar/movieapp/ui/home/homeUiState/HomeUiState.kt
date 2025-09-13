package com.karrar.movieapp.ui.home.homeUiState

import com.karrar.movieapp.ui.home.HomeItem

data class HomeUiState (
    val popularMovies: HomeItem = HomeItem.Slider(emptyList()),
    val nowStreamingMovies: HomeItem = HomeItem.NowStreaming(emptyList()),
    val adventureMovies: HomeItem = HomeItem.Adventure(emptyList()),
    val mysteryMovies: HomeItem = HomeItem.Mystery(emptyList()),
    val upcomingMovies: HomeItem = HomeItem.Upcoming(emptyList()),
    val topRatedSeries: HomeItem = HomeItem.TopRatedTvShows(emptyList()),
    val recentlyReleasedSeries: HomeItem = HomeItem.RecentlyReleased(emptyList()),
    val actors: HomeItem = HomeItem.Actor(emptyList()),
    val recentlyViewed: HomeItem = HomeItem.RecentlyViewed(emptyList()),
    val collections: HomeItem = HomeItem.Collections(emptyList()),
    val isLoading:Boolean = false,
    val error : List<String> = emptyList(),
)