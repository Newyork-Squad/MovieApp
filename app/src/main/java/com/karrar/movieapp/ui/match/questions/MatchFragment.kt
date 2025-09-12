package com.karrar.movieapp.ui.match.questions

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentMatchBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.match.questions.adapter.MatchAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchFragment : BaseFragment<FragmentMatchBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_match
    override val viewModel: MatchViewModel by viewModels()
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
                Log.d("TAG", "collectData: $state")
                questionAdapter.emitItems(state)
            }
        }
    }

    private fun render(state: MatchQuestionUiState) {
        with(binding) {
            currentMathType = state.currentQuestionType
            progressIndicator.setProgressCompat(state.progress, true)
            matchQuestionsRv.scrollToPosition(state.currentQuestionType.ordinal)
            isLoading = state.isLoading
            if (state.currentQuestionType == MatchQuestionType.RELEASE) {
                buttonContinue.setText(R.string.start_matching)
            }
        }
    }
}
