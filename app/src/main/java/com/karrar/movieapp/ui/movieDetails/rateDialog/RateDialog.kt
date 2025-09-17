package com.karrar.movieapp.ui.movieDetails.rateDialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.DialogRateBinding
import com.karrar.movieapp.ui.base.BaseDialogFragment
import com.karrar.movieapp.utilities.Constants.INPUT_RATE_KEY
import com.karrar.movieapp.utilities.Constants.RATE_DIALOG_DISMISSED_KEY
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setWidthPercent
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

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
                ratingBar.rating = state.inputRate
                val emojiRates = listOf(binding.emojiRate1, binding.emojiRate2, binding.emojiRate3, binding.emojiRate4, binding.emojiRate5)
                emojiRates.forEachIndexed { index, emoji ->
                    if (index + 1 == ceil(state.inputRate).toInt()) {
                        emoji.apply { alpha = 1f; scaleX = 1.3f; scaleY = 1.3f }
                    } else {
                        emoji.apply { alpha = 0.7f; scaleX = 1f; scaleY = 1f }
                    }
                }
                if (state.rate == 0f && state.inputRate == 0f){
                    btnRate.text = getString(R.string.add_rating)
                    btnRate.isEnabled = false
                    btnRemoveRate.visibility = View.GONE
                }
                else if (state.rate == 0f){ // inputRate != 0f
                    btnRate.text = getString(R.string.add_rating)
                    btnRate.isEnabled = true
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
                val result = Bundle().apply { putFloat(INPUT_RATE_KEY, viewModel.rateDialogUIState.value.inputRate) }
                parentFragmentManager.setFragmentResult(RATE_DIALOG_DISMISSED_KEY, result)
                dismiss()
            }

            is RateDialogUIEvent.ShowMessage -> {
                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}