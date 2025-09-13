package com.karrar.movieapp.ui.home.homeUiState

import com.karrar.movieapp.ui.home.HomeItem

data class HomeUiState (
    val popularMovies: HomeItem = HomeItem.Slider(emptyList()),
    val trendingMovies: HomeItem = HomeItem.Trending(emptyList()),
    val nowStreamingMovies: HomeItem = HomeItem.NowStreaming(emptyList()),
    val adventureMovies: HomeItem = HomeItem.Adventure(emptyList()),
    val mysteryMovies: HomeItem = HomeItem.Mystery(emptyList()),
    val upcomingMovies: HomeItem = HomeItem.Upcoming(emptyList()),
    val onTheAiringSeries: HomeItem = HomeItem.OnTheAiring(emptyList()),
    val airingTodaySeries: HomeItem = HomeItem.AiringToday(emptyList()),
    val topRatedSeries: HomeItem = HomeItem.TopRatedTvShows(emptyList()),
    val recentlyReleasedSeries: HomeItem = HomeItem.RecentlyReleased(emptyList()),
    val actors: HomeItem = HomeItem.Actor(emptyList()),
    val recentlyViewed: HomeItem = HomeItem.RecentlyViewed(emptyList()),
    val featured: HomeItem = HomeItem.FeaturedCollections(emptyList()),
    val featuredCollections: List<FeaturedCollectionUiState> = emptyList(),
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