package com.karrar.movieapp.ui.allMedia

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentAllMovieBinding
import com.karrar.movieapp.domain.enums.AllMediaType
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.home.homeUiState.FeaturedCollectionsTarget
import com.karrar.movieapp.ui.home.homeUiState.IdType
import com.karrar.movieapp.ui.models.MediaUiState
import com.karrar.movieapp.utilities.collect
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setSpanSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMovieFragment : BaseFragment<FragmentAllMovieBinding>() {
    override val layoutIdFragment = R.layout.fragment_all_movie
    override val viewModel: AllMovieViewModel by viewModels()

    private val allMediaAdapter: AllMediaAdapter by lazy { AllMediaAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = viewModel.args
        setTitle(false, getTitle(args.type , args.id , args.idType))
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        val title =getTitle(args.type , args.id , args.idType)
        binding.toolbar.title = title
        setMovieAdapter()
        setupToggle()
        collectEvent()
    }

    private fun setupToggle() {
        val toggleRoot = binding.viewToggle

        toggleRoot.ivGrid.setOnClickListener { viewModel.setGridMode(true) }
        toggleRoot.ivList.setOnClickListener { viewModel.setGridMode(false) }
        toggleRoot.indicator.setOnClickListener { viewModel.toggleGridMode() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isGrid.collect { isGrid ->
                toggleRoot.toggleMotion.transitionToState(
                    if (isGrid) R.id.start else R.id.end
                )
                allMediaAdapter.setGridMode(isGrid)

                val gridLayoutManager = GridLayoutManager(requireContext(), 2)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val isGrid = viewModel.isGrid.value
                        return if (isGrid) 1 else gridLayoutManager.spanCount
                    }
                }
                binding.recyclerMedia.layoutManager = gridLayoutManager



                val gridIcon = if (isGrid) R.drawable.ic_grid_selected else R.drawable.ic_grid_unselected
                val listIcon =
                    if (!isGrid) R.drawable.ic_row_vertical_selected else R.drawable.ic_row_vertical_unselected
                toggleRoot.ivGrid.setImageResource(gridIcon)
                toggleRoot.ivList.setImageResource(listIcon)
            }
        }
    }

    private fun setMovieAdapter() {
        val footerAdapter = LoadUIStateAdapter(allMediaAdapter::retry)
        binding.recyclerMedia.adapter = allMediaAdapter.withLoadStateFooter(footerAdapter)

        val mManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerMedia.layoutManager = mManager
        mManager.setSpanSize(footerAdapter, allMediaAdapter, mManager.spanCount)

        collect(flow = allMediaAdapter.loadStateFlow) {
            viewModel.setErrorUiState(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                uiState.allMedia.collectLatest { pagingData ->
                    setAllMedia(pagingData)
                }
            }
        }
    }

    private fun setAllMedia(itemsPagingData: PagingData<MediaUiState>) {
        allMediaAdapter.submitData(viewLifecycleOwner.lifecycle, itemsPagingData)
    }


    private fun collectEvent() {
        collectLast(viewModel.mediaUIEvent) {
            it?.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: MediaUIEvent) {
        when (event) {
            MediaUIEvent.BackEvent -> removeFragment()
            is MediaUIEvent.ClickMovieEvent -> {
                findNavController().navigate(
                    AllMovieFragmentDirections.actionAllMovieFragmentToMovieDetailFragment(
                        event.movieID
                    )
                )
            }
            is MediaUIEvent.ClickSeriesEvent -> {
                findNavController().navigate(
                    AllMovieFragmentDirections.actionAllMovieFragmentToTvShowDetailsFragment(
                        event.seriesID
                    )
                )
            }
            MediaUIEvent.RetryEvent -> allMediaAdapter.retry()
        }
    }

    private fun removeFragment() {
        findNavController().popBackStack()
    }

    private fun getTitle(type: AllMediaType, id: Int, idType: IdType): String {
        return when {
            type == AllMediaType.RECENTLY_RELEASED -> getString(R.string.title_recently_released)
            type == AllMediaType.POPULAR -> getString(R.string.popular)
            type == AllMediaType.TOP_RATED -> getString(R.string.title_top_rated_tv_show)
            type == AllMediaType.UPCOMING -> getString(R.string.title_upcoming)
            type == AllMediaType.MATCHES_YOUR_VIBE -> getString(R.string.title_matches_your_vibe)
            idType == IdType.ACTOR -> ""
            idType == IdType.GENRE && type == AllMediaType.COLLECTION_FEATURE -> {
                FeaturedCollectionsTarget.values().find { it.id == id }
                    ?.let { getString(it.title) }
                    ?: getString(R.string.featured_collections)
            }
            else -> getString(R.string.featured_collections)
        }
    }

}