package com.karrar.movieapp.ui.search

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.transition.ChangeTransform
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentSearchBinding
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.search.adapters.ActorSearchAdapter
import com.karrar.movieapp.ui.search.adapters.MediaSearchAdapter
import com.karrar.movieapp.ui.search.adapters.SearchAdapter
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaSearchUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaTypes
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.collect
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModels()
    private val mediaSearchAdapter by lazy { MediaSearchAdapter(viewModel) }
    private val actorSearchAdapter by lazy { ActorSearchAdapter(viewModel) }

    private val searchAdapter by lazy { SearchAdapter(emptyList(), viewModel) }

    private val oldValue = MutableStateFlow(MediaSearchUIState())

    private var pulseAnimator: ObjectAnimator? = null
    private var micController: MicControllerHelper? = null

    private val requestRecordPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val started = micController?.startListening() ?: false
            if (!started) {
                val intent = micController?.buildVoiceIntent(getString(R.string.search_hint))
                intent?.let { speechLauncher.launch(it) }
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_denied_for_microphone), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        hideMicOverlay()

        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                binding.inputSearch.setText(text)
                viewModel.onSearchInputChange(text)
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.no_results), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = ChangeTransform()
        setTitle(false)
        getSearchResult()
        getSearchResultsBySearchTerm()
        setupTabSelection()
        setupToggle()
        observeToggleVisibility()
        observeSearchSections()
        initMicController()

        binding.inputSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.setSearchFocus(hasFocus)
        }
        collectLast(viewModel.searchUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun initMicController() {
        micController = MicControllerHelper(requireContext(), viewLifecycleOwner)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    micController?.events?.collectLatest { event ->
                        when (event) {
                            is MicEvent.ReadyForSpeech -> showMicOverlay()
                            is MicEvent.PartialResult -> binding.inputSearch.setText(event.text)
                            is MicEvent.Result -> {
                                hideMicOverlay()
                                binding.inputSearch.setText(event.text)
                                viewModel.onSearchInputChange(event.text)
                            }

                            is MicEvent.Error -> {
                                hideMicOverlay()
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeSearchSections() {
        binding.recyclerSearchHistory.adapter = searchAdapter

        lifecycleScope.launch {
            viewModel.searchSections.collectLatest { sections ->
                searchAdapter.setItems(sections)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun getSearchResultsBySearchTerm() {
        lifecycleScope.launch {
            viewModel.uiState.debounce(500).collectLatest { searchTerm ->
                if (searchTerm.searchInput.isNotBlank()
                    && oldValue.value.searchInput != viewModel.uiState.value.searchInput
                    || oldValue.value.searchTypes != viewModel.uiState.value.searchTypes
                ) {
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

            is SearchUIEvent.ClickRecentViewedEvent -> navigateToMovieDetails(event.recentMovieViewedUiState.mediaID)

            SearchUIEvent.ClickVoiceEvent -> handleVoiceClick()
            SearchUIEvent.ClickSearchHistoryEvent -> binding.inputSearch.clearFocus()
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
            viewModel.setToggleVisibility(mediaSearchAdapter.itemCount > 0)
        }

        getMediaSearchResults()
    }

    private fun bindActors() {
        val footerAdapter = LoadUIStateAdapter(actorSearchAdapter::retry)
        binding.recyclerMedia.adapter = actorSearchAdapter.withLoadStateFooter(footerAdapter)
        binding.recyclerMedia.layoutManager = GridLayoutManager(this@SearchFragment.context, 3)
        setSpanSize(footerAdapter)

        collect(
            flow = actorSearchAdapter.loadStateFlow,
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

    private fun setupTabSelection() {
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
                        if (firstPos != RecyclerView.NO_POSITION) binding.recyclerMedia.scrollToPosition(
                            firstPos
                        )
                    }
                }
                val gridIcon =
                    if (isGrid) R.drawable.ic_grid_selected else R.drawable.ic_grid_unselected
                val listIcon =
                    if (!isGrid) R.drawable.ic_row_vertical_selected else R.drawable.ic_row_vertical_unselected
                toggleRoot.ivGrid.setImageResource(gridIcon)
                toggleRoot.ivList.setImageResource(listIcon)
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


    private fun showMicOverlay() {
        binding.includeMicOverlay.micOverlayRoot.visibility = View.VISIBLE

        pulseAnimator?.cancel()
        pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.includeMicOverlay.pulseView,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.7f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.7f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.3f)
        ).apply {
            duration = 900
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }

        binding.includeMicOverlay.micIcon.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300)
            .start()
    }

    private fun hideMicOverlay() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        binding.includeMicOverlay.micOverlayRoot.visibility = View.GONE

        binding.includeMicOverlay.micIcon.scaleX = 1f
        binding.includeMicOverlay.micIcon.scaleY = 1f
    }

    private fun handleVoiceClick() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            requestRecordPermission.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        val started = micController?.startListening() ?: false
        if (!started) {
            val intent = micController?.buildVoiceIntent(getString(R.string.search_hint))
            intent?.let { speechLauncher.launch(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            micController?.cancel()
        } catch (_: Exception) {
        }
        micController = null
    }

}
