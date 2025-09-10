package com.karrar.movieapp.ui.explore

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentExploringBinding
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.category.CategoryAdapter
import com.karrar.movieapp.ui.explore.exploreUIState.ExploringUIEvent
import com.karrar.movieapp.ui.explore.exploreUIState.TrendyMediaUIState
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.collect
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setSpanSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ExploringFragment : BaseFragment<FragmentExploringBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_exploring
    override val viewModel: ExploringViewModel by viewModels()

    private val allMediaAdapter by lazy { CategoryAdapter(viewModel) }
    private lateinit var genreAdapter: GenreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(false)
        collectEvent()
        initTabLayout()
        setupSearchHideOnScroll()
        setGenreAdapter()
        setMediaAdapter()
        collectData()
    }

    private fun initTabLayout() {
        binding.tabExplore.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> viewModel.setMediaType(Constants.MOVIE_CATEGORIES_ID)
                    1 -> viewModel.setMediaType(Constants.TV_CATEGORIES_ID)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setGenreAdapter() {
        genreAdapter = GenreAdapter(emptyList(), viewModel)

        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
        }
    }

    private fun setMediaAdapter() {
        val footerAdapter = LoadUIStateAdapter(allMediaAdapter::retry)
        binding.recyclerMedia.adapter = allMediaAdapter.withLoadStateFooter(footerAdapter)

        val mManager = binding.recyclerMedia.layoutManager as GridLayoutManager
        mManager.setSpanSize(footerAdapter, allMediaAdapter, mManager.spanCount)

        collect(
            flow = allMediaAdapter.loadStateFlow,
            action = { viewModel.setErrorUiState(it) })
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                genreAdapter.setItems(state.genre)
                genreAdapter.setSelectedGenre(state.selectedCategoryID)
                collectLast(viewModel.uiState.value.media) { paging ->
                    allMediaAdapter.submitData(paging)
                }
            }
        }
    }

    private fun setupSearchHideOnScroll() {
        val HIDE_THRESHOLD = 20
        var scrolledDistance = 0
        var controlsVisible = true

        binding.recyclerMedia.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy
                } else {
                    scrolledDistance = 0
                }

                if (controlsVisible && scrolledDistance > HIDE_THRESHOLD) {
                    binding.motionLayout.transitionToEnd()
                    controlsVisible = false
                    scrolledDistance = 0
                } else if (!controlsVisible && scrolledDistance < -HIDE_THRESHOLD) {
                    binding.motionLayout.transitionToStart()
                    controlsVisible = true
                    scrolledDistance = 0
                }
            }

        })
    }

    private fun collectEvent() {
        collectLast(viewModel.exploringUIEvent) {
            it?.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: ExploringUIEvent) {
        when (event) {
            ExploringUIEvent.SearchEvent -> navigateToSearch()
            is ExploringUIEvent.OpenDetails -> {
                if (event.isTv) {
                    findNavController().navigate(
                        ExploringFragmentDirections.actionExploringFragmentToTvShowDetailsFragment(
                            event.mediaId
                        )
                    )
                } else {
                    findNavController().navigate(
                        ExploringFragmentDirections.actionExploringFragmentToMovieDetailFragment(
                            event.mediaId
                        )
                    )
                }
            }
        }
    }

    private fun navigateToSearch() {
        val extras = FragmentNavigatorExtras(binding.inputSearch to "search_box")
        Navigation.findNavController(binding.root)
            .navigate(
                ExploringFragmentDirections.actionExploringFragmentToSearchFragment(),
                extras
            )
    }

    private fun navigateToMediaDetails(item: TrendyMediaUIState) {
        when (item.mediaType) {
            Constants.MOVIE -> {
                findNavController().navigate(
                    ExploringFragmentDirections.actionExploringFragmentToMovieDetailFragment(
                        item.mediaID
                    )
                )
            }

            Constants.TV_SHOWS -> {
                findNavController().navigate(
                    ExploringFragmentDirections.actionExploringFragmentToTvShowDetailsFragment(
                        item.mediaID
                    )
                )
            }
        }
    }

}