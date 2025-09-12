package com.karrar.movieapp.ui.tvShowDetails.seasons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentSeasonsBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeasonsFragment : BaseFragment<FragmentSeasonsBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_seasons
    override val viewModel: SeasonsViewModel by viewModels()
    private val seasonsAdapter by lazy { ShowAllSeasonsAdapterUIState(emptyList(), viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = viewModel
        binding.seasonsAdapter.adapter = seasonsAdapter
        collectEvents()
    }

    private fun collectEvents() {
        collectLast(viewModel.seasonsUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: SeasonsUIEvent) {
        when (event) {
            SeasonsUIEvent.OnBackClick -> {
                findNavController().navigateUp()
            }
        }
    }
}