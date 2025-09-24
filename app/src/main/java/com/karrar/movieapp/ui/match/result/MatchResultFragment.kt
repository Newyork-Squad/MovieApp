package com.karrar.movieapp.ui.match.result

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentMatchResultBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.match.questions.MatchQuestionsViewModel
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MatchResultFragment : BaseFragment<FragmentMatchResultBinding>() {

    override val layoutIdFragment: Int = R.layout.fragment_match_result
    override val viewModel: MatchQuestionsViewModel by activityViewModels()

    private lateinit var pagerAdapter: MoviePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("tst-onv-movies","${viewModel.uiState.value.movies}")
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
                Log.d("tst-fragment","${state.movies}")
            // <--- IMPORTANT
            }
        }

        // collect current movie details
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                val currentMovie = state.movies.getOrNull(state.selectedMovieIndex)
                binding.movieDetailsCard.apply {
                    setVariable(BR.item, currentMovie?.movieDetailsResult)
                    executePendingBindings()
                }
            }
        }

        collectLast(viewModel.matchResultUiEvent) { event ->
            event.getContentIfNotHandled()?.let { handleEvent(it) }
        }

        binding.backArrowIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleEvent(event: MatchUiEvent) {
        Log.d("back-before when","event $event")
        when (event) {
            MatchUiEvent.NavigateBack -> {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
             is MatchUiEvent.PlayYoutubeTrailer -> {
            }

            is MatchUiEvent.SaveMovie -> {

            }

            MatchUiEvent.ViewMovieDetails -> {
            }

            MatchUiEvent.NavigateToResults -> TODO()
            MatchUiEvent.ShowNoMoviesToast -> TODO()
        }
    }
}

