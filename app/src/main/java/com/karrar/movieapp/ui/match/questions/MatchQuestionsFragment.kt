package com.karrar.movieapp.ui.match.questions

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentMatchQuestionsBinding
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.match.MatchUiState
import com.karrar.movieapp.ui.match.MatchViewModel
import com.karrar.movieapp.ui.match.questions.adapter.MatchAdapter
import com.karrar.movieapp.ui.match.result.MatchResultFragmentDirections
import com.karrar.movieapp.ui.match.result.MatchUiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchQuestionsFragment : BaseFragment<FragmentMatchQuestionsBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_match_questions
    override val viewModel: MatchViewModel by activityViewModels()
    var currentMathType = MatchQuestionType.MOOD
    private lateinit var questionAdapter: MatchAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData()
        questionAdapter =
            MatchAdapter(
                mutableListOf(),
                viewModel,
            ) { selectedChoices, type ->
                viewModel.onChoiceSelected(type, selectedChoices)
            }
        binding.vm = viewModel
        binding.matchQuestionsRv.adapter = questionAdapter

        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }

        lifecycleScope.launch {
            viewModel.questions.collectLatest { state ->
                questionAdapter.emitItems(state)
            }
        }
        lifecycleScope.launch {
            viewModel.matchResultUiEvent.collect { eventWrapper ->
                val event = eventWrapper.getContentIfNotHandled() ?: return@collect

                when (event) {
                    MatchUiEvent.NavigateToResults -> {
                        val action =
                            MatchQuestionsFragmentDirections.actionMatchQuestionsFragmentToMatchResultFragment()
                        findNavController().navigate(action)
                    }

                    MatchUiEvent.ShowNoMoviesToast -> {
                        findNavController().popBackStack()
                        Toast
                            .makeText(
                                requireContext(),
                                getString(R.string.no_matching_movies_found),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
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
                }
            }
        }
    }

    private fun render(state: MatchUiState) {
        with(binding) {
            currentMathType = state.currentQuestionType
            progressIndicator.setProgressCompat(state.progress, true)
            matchQuestionsRv.scrollToPosition(state.currentQuestionType.ordinal)
            isLoading = state.isLoading
        }
    }
}
