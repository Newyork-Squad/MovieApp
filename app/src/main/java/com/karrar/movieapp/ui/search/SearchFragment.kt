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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentSearchBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.search.adapters.SearchAdapter
import com.karrar.movieapp.ui.search.adapters.SuggestionsAdapter
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchItemUiState
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModels()


    private val suggestionsAdapter by lazy { SuggestionsAdapter(viewModel) }

    private val searchAdapter by lazy { SearchAdapter(emptyList(), viewModel,suggestionsAdapter) }

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

        binding.recyclerSearchHistory.adapter = searchAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchSections.collect { sections ->
                searchAdapter.setItems(sections)
            }
        }

        binding.inputSearch.addTextChangedListener { editable ->
            viewModel.onSearchInputChange(editable.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchSections.collect { sections ->
                searchAdapter.setItems(sections)

                sections.filterIsInstance<SearchItemUiState.SuggestionsItems>()
                    .firstOrNull()?.let { suggestions ->
                        launch {
                            suggestions.data.collectLatest { pagingData ->
                                suggestionsAdapter.submitData(pagingData)
                            }
                        }
                    }
            }
        }


        binding.inputSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.inputSearch.text.toString()
            if (query.isNotBlank()) {
                try {
                    val action = SearchFragmentDirections.actionSearchFragmentToResultSearch()
                    action.query = query
                    findNavController().navigate(action)
                } catch (e: IllegalArgumentException) {
                    val bundle = Bundle().apply { putString("query", query) }
                    findNavController().navigate(R.id.resultSearch, bundle)
                }
            }
            true
        }
        sharedElementEnterTransition = ChangeTransform()
        setTitle(false)
        observeSearchSections()
        initMicController()
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
                viewModel.getData()
            }

            is SearchUIEvent.ClickRecentViewedEvent -> navigateToMovieDetails(event.recentMovieViewedUiState.mediaID)

            SearchUIEvent.ClickVoiceEvent -> handleVoiceClick()

            is SearchUIEvent.ClickSearchEvent -> {
                val action = SearchFragmentDirections.actionSearchFragmentToResultSearch()
                action.query = event.query
                findNavController().navigate(action)
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



    private fun popFragment() {
        findNavController().popBackStack()
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
