package com.karrar.movieapp.ui.match.questions

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentMatchQuestionsBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.match.questions.adapter.MatchAdapter
import com.karrar.movieapp.ui.match.result.MatchUiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchQuestionsFragment : BaseFragment<FragmentMatchQuestionsBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_match_questions
    override val viewModel: MatchQuestionsViewModel by activityViewModels()
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
                        val action = MatchQuestionsFragmentDirections
                            .actionMatchQuestionsFragmentToMatchResultFragment()
                        findNavController().navigate(action)
                    }

                    MatchUiEvent.ShowNoMoviesToast -> {
                        Toast.makeText(
                            requireContext(),
                            "No matching movies found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    MatchUiEvent.NavigateBack -> TODO()
                    is MatchUiEvent.PlayYoutubeTrailer -> TODO()
                    is MatchUiEvent.SaveMovie -> TODO()
                    MatchUiEvent.ViewMovieDetails -> TODO()
                }
            }
        }
    }

    private fun render(state: MatchQuestionUiState) {
        with(binding) {
            currentMathType = state.currentQuestionType
            progressIndicator.setProgressCompat(state.progress, true)
            matchQuestionsRv.scrollToPosition(state.currentQuestionType.ordinal)
            isLoading = state.isLoading
        }
    }
}
