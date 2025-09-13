package com.karrar.movieapp.ui.movieDetails.saveMovie

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.DialogSaveMovieBinding
import com.karrar.movieapp.ui.base.BaseDialogFragment
import com.karrar.movieapp.ui.movieDetails.saveMovie.uiState.SaveMovieUIEvent
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SaveMovieDialog : BaseDialogFragment<DialogSaveMovieBinding>() {
    override val layoutIdFragment = R.layout.dialog_save_movie
    override val viewModel: SaveMovieViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = viewModel

        val params = (view.parent as View).layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            resources.getDimensionPixelSize(R.dimen.spacing_normal),
            resources.getDimensionPixelSize(R.dimen.spacing_normal),
            resources.getDimensionPixelSize(R.dimen.spacing_normal),
            resources.getDimensionPixelSize(R.dimen.spacing_large)
        )


        binding.saveListAdapter.adapter = SaveListAdapter(mutableListOf(), viewModel)
        collectLast(viewModel.saveMovieUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }

        }
    }

    private fun onEvent(event: SaveMovieUIEvent) {
//        var action: NavDirections? = null
        when (event) {
            is SaveMovieUIEvent.DisplayMessage -> {
                if (event.message.isNotBlank())
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                dismiss()
            }

            is SaveMovieUIEvent.NavigateToCollectionScreen -> {
                findNavController().navigate(
                    SaveMovieDialogDirections.actionSaveMovieDialogToMyListFragment()
                )
                dismiss()
            }
        }
//        action?.let { findNavController().navigate(it) }
    }
}