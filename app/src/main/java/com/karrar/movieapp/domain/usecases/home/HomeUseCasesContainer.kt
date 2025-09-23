package com.karrar.movieapp.domain.usecases.home

import com.karrar.movieapp.domain.usecase.home.getData.movie.GetPopularMoviesUseCase
import com.karrar.movieapp.domain.usecase.home.getData.movie.GetUpcomingMoviesUseCase
import com.karrar.movieapp.domain.usecase.home.getData.series.GetAiringTodaySeriesUseCase
import com.karrar.movieapp.domain.usecase.home.getData.series.GetTopRatedTvShowSeriesUseCase
import com.karrar.movieapp.domain.usecases.GetWatchHistoryUseCase
import com.karrar.movieapp.domain.usecases.home.getData.movie.GetMoviesMatchingUserVibeUseCase
import javax.inject.Inject

class HomeUseCasesContainer @Inject constructor(
    val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    val getAiringTodayUseCase: GetAiringTodaySeriesUseCase,
    val getTopRatedTvShowUseCase: GetTopRatedTvShowSeriesUseCase,
    val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    val getWatchHistoryUseCase: GetWatchHistoryUseCase,
    val getMoviesMatchingUserVibeUseCase: GetMoviesMatchingUserVibeUseCase
)