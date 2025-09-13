package com.karrar.movieapp.ui.movieDetails.rateDialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.DialogRateBinding
import com.karrar.movieapp.ui.base.BaseDialogFragment
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setWidthPercent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RateDialog : BaseDialogFragment<DialogRateBinding>() {

    override val layoutIdFragment = R.layout.dialog_rate
    override val viewModel: RateDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)
        collectLast(viewModel.rateDialogUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.onStarClick(rating)
        }

        binding.btnRate.setOnClickListener {
            viewModel.onSubmitClick()
        }

        binding.btnRemoveRate.setOnClickListener {
            viewModel.onDeleteClick()
        }

        binding.btnClose.setOnClickListener {
            viewModel.onCancelClick()
        }

    }

    private fun onEvent(event: RateDialogUIEvent) {
        when (event) {
            is RateDialogUIEvent.CloseDialog -> {
                parentFragmentManager.setFragmentResult("rate_dialog_dismissed", Bundle())
                dismiss()
            }

            is RateDialogUIEvent.ShowMessage -> {
                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}