package com.karrar.movieapp.ui.movieDetails.rateDialog

import android.annotation.SuppressLint
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

        // collect UI state
        collectLast(viewModel.rateDialogUIState) { state ->
            binding.apply {
                ratingBar.rating = state.rate
                if (state.rate == 0f){
                    btnRate.text = getString(R.string.add_rating)
                    btnRate.isEnabled = false
                    btnRemoveRate.visibility = View.GONE
                }
                else{
                    btnRate.text = getString(R.string.change_rating)
                    btnRate.isEnabled = true
                    btnRemoveRate.visibility = View.VISIBLE
                }
            }
        }

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
                dismiss()
            }

            is RateDialogUIEvent.ShowMessage -> {
                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}