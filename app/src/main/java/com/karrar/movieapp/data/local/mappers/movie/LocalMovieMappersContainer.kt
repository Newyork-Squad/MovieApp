package com.karrar.movieapp.data.local.mappers.movie

import com.karrar.movieapp.data.local.mappers.ActorMapper

import javax.inject.Inject

class LocalMovieMappersContainer @Inject constructor(
    val popularMovieMapper: PopularMovieMapper,
    val trendingMovieMapper: TrendingMovieMapper,
    val recentMovieViewedMapper: RecentMovieViewedMapper,
    val nowStreamingMovieMapper: NowStreamingMovieMapper,
    val upcomingMovieMapper: UpcomingMovieMapper,
    val mysteryMovieMapper: MysteryMovieMapper,
    val adventureMovieMapper: AdventureMovieMapper,
    val actorMapper: ActorMapper,
)