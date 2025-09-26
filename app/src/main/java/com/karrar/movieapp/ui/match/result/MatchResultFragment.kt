package com.karrar.movieapp.ui.match.result

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentMatchResultBinding
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.match.MatchViewModel
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MatchResultFragment : BaseFragment<FragmentMatchResultBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_match_result
    override val viewModel: MatchViewModel by activityViewModels()

    private lateinit var pagerAdapter: MoviePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(false)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        pagerAdapter = MoviePagerAdapter(emptyList(), viewModel)
        binding.horizontalPager.adapter = pagerAdapter
        binding.horizontalPager.offscreenPageLimit = 3

        val nextItemVisiblePx = resources.displayMetrics.density * 60   // space for peeking
        val currentItemHorizontalMarginPx = resources.displayMetrics.density * 10
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx

        binding.horizontalPager.setPageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            // scaling
            val scale = 0.85f + (1 - abs(position)) * 0.15f
            page.scaleY = scale
            page.scaleX = scale
            // fading
            page.alpha = 0.6f + (1 - abs(position)) * 0.4f

            page.translationZ = if (position == 0f) 1f else 0f
        }

        // page change updates selected movie
        binding.horizontalPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.updateSelectedMovie(position)
            }
        })

        // collect movie list
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                pagerAdapter.setItems(state.movies.map {movie -> movie.movieDetailsResult.image})
            // <--- IMPORTANT
            }
        }

        // collect current movie details
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                val currentMovie = state.movies.getOrNull(state.selectedMovieIndex)
                binding.movieDetailsCard.apply {
                    setVariable(BR.item, currentMovie?.movieDetailsResult)
                    setVariable(BR.listener, viewModel)
                    executePendingBindings()
                }
            }
        }

        collectLast(viewModel.matchResultUiEvent) { event ->
            event.getContentIfNotHandled()?.let { handleEvent(it) }
        }
    }

    private fun handleEvent(event: MatchUiEvent) {
        when (event) {
            MatchUiEvent.NavigateBack -> {
                findNavController().popBackStack(R.id.homeFragment, false)
            }

            is MatchUiEvent.PlayYoutubeTrailer -> {
                val action =
                    MatchResultFragmentDirections
                        .actionMatchResultFragmentToYoutubePlayerActivity(
                            event.movieId,
                            MediaType.MOVIE,
                        )

                findNavController().navigate(action)
            }

            is MatchUiEvent.SaveMovie -> {
                val action =
                    MatchResultFragmentDirections
                        .actionMatchResultFragmentToSaveMovieDialog(event.movieId)
                findNavController().navigate(action)
            }

            is MatchUiEvent.ViewMovieDetails -> {
                val action =
                    MatchResultFragmentDirections
                        .actionMatchResultFragmentToMovieDetailFragment(event.movieId)
                findNavController().navigate(action)
            }

            MatchUiEvent.ShowLoginDialogEvent -> {
                val action =
                    MatchResultFragmentDirections
                        .actionMatchResultFragmentToLoginDialog()
                findNavController().navigate(action)
            }

            MatchUiEvent.NavigateToResults -> TODO()
            MatchUiEvent.ShowNoMoviesToast -> TODO()
        }
    }
}

