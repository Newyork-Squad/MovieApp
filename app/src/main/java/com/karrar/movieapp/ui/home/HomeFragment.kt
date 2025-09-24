package com.karrar.movieapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentHomeBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.home.adapter.HomeAdapter
import com.karrar.movieapp.ui.home.homeUiState.HomeUIEvent
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutIdFragment = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels()
    lateinit var homeAdapter: HomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(false)
        setAdapter()
        collectEvent()
        collectHomeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshHomeData()
    }

    private fun collectHomeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeUiState.collect {
                homeAdapter.setItems(
                    mutableListOf(
                        it.popularMovies,
                        it.recentlyReleasedSeries,
                        it.topRatedSeries,
                        it.upcomingMovies,
                        it.matchedItems,
                        it.recentlyViewed,
                        it.collections,
                        it.whatShouldIWatch,
                        it.needMoreToWatch,
                        it.featured,

                    )
                )
            }
        }
    }

    private fun setAdapter() {
        homeAdapter = HomeAdapter(mutableListOf(), viewModel)
        binding.recyclerView.adapter = homeAdapter
    }

    private fun collectEvent() {
        collectLast(viewModel.homeUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: HomeUIEvent) {
        val action = when (event) {
            is HomeUIEvent.ClickActorEvent -> {
                HomeFragmentDirections.actionHomeFragmentToActorDetailsFragment(
                    event.actorID
                )
            }
            is HomeUIEvent.ClickMovieEvent -> {
                HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(
                    event.movieID
                )
            }
            HomeUIEvent.ClickSeeAllActorEvent -> {
                HomeFragmentDirections.actionHomeFragmentToActorsFragment()
            }
            is HomeUIEvent.ClickSeeAllMovieEvent -> {
                HomeFragmentDirections.actionHomeFragmentToAllMovieFragment(
                    -1, event.mediaType
                )
            }
            is HomeUIEvent.ClickSeeAllTVShowsEvent -> {
                HomeFragmentDirections.actionHomeFragmentToAllMovieFragment(
                    -1,
                    event.mediaType
                )
            }
            is HomeUIEvent.ClickSeriesEvent -> {
                HomeFragmentDirections.actionHomeFragmentToTvShowDetailsFragment(
                    event.seriesID
                )
            }

            HomeUIEvent.clickToMatchScreen->HomeFragmentDirections.actionHomeFragmentToMatchScreenFragment()

            HomeUIEvent.ClickSeeAllRecentlyViewed -> HomeFragmentDirections.actionHomeFragmentToWatchHistoryFragment()
            HomeUIEvent.clickToExploreScreen->HomeFragmentDirections.actionHomeFragmentToExploringFragment()


            HomeUIEvent.ClickSeeAllCollections -> HomeFragmentDirections.actionHomeFragmentToSavedListFragment()

            is HomeUIEvent.ClickListEvent -> HomeFragmentDirections.actionHomeFragmentToListDetailsFragment(
                event.createdListUIState.listID,
                event.createdListUIState.name
            )

            is HomeUIEvent.ClickFeaturedCollection ->  HomeFragmentDirections.actionHomeFragmentToWatchHistoryFragment()
        }
        findNavController().navigate(action)
    }

    fun refreshHomeData() {
        viewModel.refreshHomeData()
    }


}