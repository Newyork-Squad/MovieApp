package com.karrar.movieapp.ui.search.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentResultSearchBinding
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.search.adapters.ActorSearchAdapter
import com.karrar.movieapp.ui.search.adapters.MediaSearchAdapter
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaTypes
import com.karrar.movieapp.utilities.collect
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentResultSearchBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_result_search
    override val viewModel: SearchResultViewModel by viewModels()

    private val args: SearchResultFragmentArgs by navArgs()

    private val mediaSearchAdapter by lazy { MediaSearchAdapter(viewModel) }
    private val actorSearchAdapter by lazy { ActorSearchAdapter(viewModel) }

    private var isFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView(MediaTypes.MOVIE)
        viewModel.setSearchInput(args.query)
        observeSelectedTab()
        observeUiState()
        observeEvents()
    }

    private fun setupUI() {
        setupTabSelection()
        setupToggle()
        observeToggleVisibility()
    }

    private fun observeSelectedTab() {
        lifecycleScope.launch {
            viewModel.selectedTabIndex.collect { tabIndex ->
                if (!isFirstTime || binding.tabMediaType.selectedTabPosition != tabIndex) {
                    binding.tabMediaType.getTabAt(tabIndex)?.select()
                    setupRecyclerView(getMediaTypeFromTab(tabIndex))
                }
                isFirstTime = false
            }
        }
    }

    private fun setupTabSelection() {
        binding.tabMediaType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabIndex = tab?.position ?: 0
                viewModel.setSelectedTabIndex(tabIndex)
                val type = getMediaTypeFromTab(tabIndex)
                viewModel.setMediaType(type)
                setupRecyclerView(type)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView(type: MediaTypes) {
        val isGridMode = viewModel.isGrid.value
        val spanCount = if (isGridMode) 2 else 1

        binding.recyclerMedia.layoutManager =
            GridLayoutManager(requireContext(), spanCount, RecyclerView.VERTICAL, false)

        viewModel.setToggleVisibility(type != MediaTypes.ACTOR)

        if (type == MediaTypes.ACTOR) {
            binding.recyclerMedia.adapter = actorSearchAdapter
        } else {
            val footerAdapter = LoadUIStateAdapter(mediaSearchAdapter::retry)
            binding.recyclerMedia.adapter = mediaSearchAdapter.withLoadStateFooter(footerAdapter)
            collect(flow = mediaSearchAdapter.loadStateFlow) {
                viewModel.setErrorState(it, mediaSearchAdapter.itemCount)
            }
        }
    }


    private fun setupToggle() {
        val toggleRoot = binding.viewToggle

        toggleRoot.ivGrid.setOnClickListener { viewModel.setGridMode(true) }
        toggleRoot.ivList.setOnClickListener { viewModel.setGridMode(false) }
        toggleRoot.indicator.setOnClickListener { viewModel.toggleGridMode() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isGrid.collect { isGrid ->
                // MotionLayout Animation
                if (isGrid) toggleRoot.toggleMotion.transitionToStart()
                else toggleRoot.toggleMotion.transitionToEnd()

                mediaSearchAdapter.setGridMode(isGrid)

                val lm = binding.recyclerMedia.layoutManager as? GridLayoutManager
                lm?.let {
                    val firstPos = it.findFirstVisibleItemPosition()
                    it.spanCount = if (isGrid) 2 else 1

                    binding.recyclerMedia.post {
                        it.requestLayout()
                        if (firstPos != RecyclerView.NO_POSITION) {
                            binding.recyclerMedia.scrollToPosition(firstPos)
                        }
                    }
                }

                toggleRoot.ivGrid.setImageResource(
                    if (isGrid) R.drawable.ic_grid_selected else R.drawable.ic_grid_unselected
                )
                toggleRoot.ivList.setImageResource(
                    if (!isGrid) R.drawable.ic_row_vertical_selected else R.drawable.ic_row_vertical_unselected
                )
            }
        }
    }

    private fun observeToggleVisibility() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showToggle.collect { visible ->
                binding.viewToggle.root.visibility = if (visible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { ui ->
                ui.searchResult?.let { pagingData ->
                    if (ui.searchTypes == MediaTypes.ACTOR) {
                        actorSearchAdapter.submitData(pagingData)
                    } else {
                        mediaSearchAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        collectLast(viewModel.searchResultEvent) { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    is SearchResultEvent.ClickMedia -> {
                        val type = when (it.media.mediaTypes.lowercase()) {
                            "movie" -> MediaType.MOVIE
                            "tv" -> MediaType.TV_SHOW
                            else -> null
                        }

                        when (type) {
                            MediaType.MOVIE -> findNavController().navigate(
                                SearchResultFragmentDirections
                                    .actionResultSearchToMovieDetailFragment(it.media.mediaID)
                            )

                            MediaType.TV_SHOW -> findNavController().navigate(
                                SearchResultFragmentDirections
                                    .actionResultSearchToTvShowDetailsFragment(it.media.mediaID)
                            )

                            null -> {  }
                        }
                    }

                    is SearchResultEvent.ClickActor -> findNavController().navigate(
                        SearchResultFragmentDirections
                            .actionResultSearchToActorDetailsFragment(it.actorId)
                    )

                    SearchResultEvent.ClickBack -> findNavController().popBackStack()
                }
            }
        }
    }



    private fun getMediaTypeFromTab(position: Int): MediaTypes {
        return when (position) {
            0 -> MediaTypes.MOVIE
            1 -> MediaTypes.TVS_SHOW
            else -> MediaTypes.ACTOR
        }
    }
}