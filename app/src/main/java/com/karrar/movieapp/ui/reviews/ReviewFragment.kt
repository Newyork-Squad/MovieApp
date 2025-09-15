package com.karrar.movieapp.ui.reviews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentReviewBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_review
    override val viewModel: ReviewViewModel by viewModels()
    private val reviewAdapter by lazy { ReviewAdapter(emptyList(), viewModel) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = viewModel
        binding.commentReviewAdapter.adapter = reviewAdapter
        collectEvents()
    }

    private fun collectEvents() {
        collectLast(viewModel.reviewUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: ReviewUIEvent) {
        when (event) {
            ReviewUIEvent.ClickBackEvent -> {
                findNavController().navigateUp()
            }
        }
    }
}