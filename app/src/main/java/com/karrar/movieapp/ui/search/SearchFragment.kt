package com.karrar.movieapp.ui.search

import android.content.Context
import android.os.Bundle
import android.transition.ChangeTransform
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.tabs.TabLayout
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentSearchBinding
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.search.adapters.*
import com.karrar.movieapp.ui.search.mediaSearchUIState.*
import com.karrar.movieapp.utilities.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModels()

    private val mediaSearchAdapter by lazy { MediaSearchAdapter(viewModel) }
    private val actorSearchAdapter by lazy { ActorSearchAdapter(viewModel) }

    private val oldValue = MutableStateFlow(MediaSearchUIState())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = ChangeTransform()
        setTitle(false)
        getSearchResult()
        setSearchHistoryAdapter()
        getSearchResultsBySearchTerm()
        setupTabSelection()
        setupToggle()
        collectLast(viewModel.searchUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun setSearchHistoryAdapter() {
        val inputMethodManager =
            binding.inputSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.inputSearch, InputMethodManager.SHOW_IMPLICIT)

        binding.recyclerSearchHistory.adapter = SearchHistoryAdapter(mutableListOf(), viewModel)
    }

    @OptIn(FlowPreview::class)
    private fun getSearchResultsBySearchTerm() {
        lifecycleScope.launch {
            viewModel.uiState.debounce(500).collectLatest { searchTerm ->
                if (searchTerm.searchInput.isNotBlank()
                    && oldValue.value.searchInput != viewModel.uiState.value.searchInput
                    || oldValue.value.searchTypes != viewModel.uiState.value.searchTypes) {
                    getSearchResult()
                    oldValue.emit(viewModel.uiState.value)
                }
            }
        }
    }

    private fun getSearchResult() {
        when (viewModel.uiState.value.searchTypes) {
            MediaTypes.ACTOR -> {
                binding.viewToggle.root.visibility = View.GONE
                bindActors()
            }
            else -> {
                binding.viewToggle.root.visibility = View.VISIBLE
                bindMedia()
            }
        }
    }


    private fun onEvent(event: SearchUIEvent) {
        when (event) {
            is SearchUIEvent.ClickActorEvent -> {
                navigateToActorDetails(event.actorID)
            }
            SearchUIEvent.ClickBackEvent -> {
                popFragment()
            }
            is SearchUIEvent.ClickMediaEvent -> {
                when (event.mediaUIState.mediaTypes) {
                    Constants.MOVIE -> navigateToMovieDetails(event.mediaUIState.mediaID)
                    Constants.TV_SHOWS -> navigateToSeriesDetails(event.mediaUIState.mediaID)
                }
            }
            SearchUIEvent.ClickRetryEvent -> {
                actorSearchAdapter.retry()
                mediaSearchAdapter.retry()
            }
        }
    }

    private fun navigateToMovieDetails(movieId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(
                movieId
            )
        )
    }

    private fun navigateToSeriesDetails(seriesId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToTvShowDetailsFragment(
                seriesId
            )
        )
    }

    private fun navigateToActorDetails(actorId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToActorDetailsFragment(
                actorId
            )
        )
    }

    private fun bindMedia() {
        val footerAdapter = LoadUIStateAdapter(mediaSearchAdapter::retry)
        binding.recyclerMedia.adapter = mediaSearchAdapter.withLoadStateFooter(footerAdapter)

        val isGrid = viewModel.isGrid.value
        binding.recyclerMedia.layoutManager =
            if (isGrid) GridLayoutManager(requireContext(), 2)
            else LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        collect(flow = mediaSearchAdapter.loadStateFlow) {
            viewModel.setErrorUiState(it, mediaSearchAdapter.itemCount)
        }

        getMediaSearchResults()
    }


    private fun bindActors() {
        val footerAdapter = LoadUIStateAdapter(actorSearchAdapter::retry)
        binding.recyclerMedia.adapter = actorSearchAdapter.withLoadStateFooter(footerAdapter)
        binding.recyclerMedia.layoutManager = GridLayoutManager(this@SearchFragment.context, 3)
        setSpanSize(footerAdapter)

        collect(flow = actorSearchAdapter.loadStateFlow,
            action = { viewModel.setErrorUiState(it, actorSearchAdapter.itemCount) })

        getActorsSearchResults()
    }

    private fun getMediaSearchResults() {
        collectLast(viewModel.uiState.value.searchResult)
        { mediaSearchAdapter.submitData(it) }
    }

    private fun getActorsSearchResults() {
        collectLast(viewModel.uiState.value.searchResult)
        { actorSearchAdapter.submitData(it) }
    }

    private fun setSpanSize(footerAdapter: LoadUIStateAdapter) {
        val mManager = binding.recyclerMedia.layoutManager as GridLayoutManager
        mManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == actorSearchAdapter.itemCount)
                    && footerAdapter.itemCount > 0
                ) {
                    mManager.spanCount
                } else {
                    1
                }
            }
        }
    }

    private fun popFragment() {
        findNavController().popBackStack()
    }

    private fun setupTabSelection(){
        binding.tabMediaType.getTabAt(0)?.select()

        binding.tabMediaType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.onSearchForMovie()
                    1 -> viewModel.onSearchForSeries()
                    2 -> viewModel.onSearchForActor()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    private fun setupToggle() {
        val toggleRoot = binding.viewToggle

        toggleRoot.ivGrid.setOnClickListener { viewModel.setGridMode(true) }
        toggleRoot.ivList.setOnClickListener { viewModel.setGridMode(false) }
        toggleRoot.indicator.setOnClickListener { viewModel.toggleGridMode() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isGrid.collect { isGrid ->
                if (isGrid) toggleRoot.toggleMotion.transitionToStart() else toggleRoot.toggleMotion.transitionToEnd()

                mediaSearchAdapter.setGridMode(isGrid)

                val lm = binding.recyclerMedia.layoutManager as? GridLayoutManager
                lm?.let {
                    val firstPos = it.findFirstVisibleItemPosition()
                    it.spanCount = if (isGrid) 2 else 1

                    binding.recyclerMedia.post {
                        it.requestLayout()
                        if (firstPos != RecyclerView.NO_POSITION) binding.recyclerMedia.scrollToPosition(firstPos)
                    }
                }
                val gridIcon = if (isGrid) R.drawable.ic_grid_selected else R.drawable.ic_grid_unselected
                val listIcon = if (!isGrid) R.drawable.ic_row_vertical_selected else R.drawable.ic_row_vertical_unselected
                toggleRoot.ivGrid.setImageResource(gridIcon)
                toggleRoot.ivList.setImageResource(listIcon)
            }
        }
    }

}