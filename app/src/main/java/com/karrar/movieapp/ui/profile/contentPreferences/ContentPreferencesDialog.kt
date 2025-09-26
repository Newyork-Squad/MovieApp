package com.karrar.movieapp.ui.profile.contentPreferences

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.BottomSheetContentPreferencesBinding
import com.karrar.movieapp.ml.StrengthLevel
import com.karrar.movieapp.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ContentPreferencesDialog : BaseDialog<BottomSheetContentPreferencesBinding>() {

    override val layoutIdFragment: Int = R.layout.bottom_sheet_content_preferences
    private val viewModel: ContentPreferenceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        lifecycleScope.launchWhenStarted {
            updateCardSelection(viewModel.selectedPreference.value)
            viewModel.selectedPreference.collectLatest { updateCardSelection(it) }        }

        binding.hideExplicitContent.setOnClickListener {
            viewModel.selectPreference(StrengthLevel.HIDE_EXPLICIT)
            dismiss()
        }

        binding.strictFilteringContent.setOnClickListener {
            viewModel.selectPreference(StrengthLevel.STRICT)
            dismiss()
        }

        binding.showAllContent.setOnClickListener {
            viewModel.selectPreference(StrengthLevel.SHOW_ALL)
            dismiss()
        }

        binding.iconClose.setOnClickListener {
            viewModel.closeDialog()
            dismiss()
        }
    }

    private fun updateCardSelection(selected: StrengthLevel?) {
        val borderColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)

        // Hide Explicit Content
        binding.hideExplicitContent.strokeWidth = if (selected == StrengthLevel.HIDE_EXPLICIT) 4 else 0
        binding.hideExplicitContent.strokeColor = borderColor
        binding.hideExplicitContent.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.HIDE_EXPLICIT) R.color.brand_tertiary else R.color.background_bottomSheetCard
        )
        binding.textHideExplicit.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.HIDE_EXPLICIT) R.color.brand_primary else R.color.shade_primary
            )
        )
        binding.textHideExplicitDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.HIDE_EXPLICIT) R.color.brand_primary else R.color.shade_secondary
            )
        )
        // Frame & Image
        binding.frameHideExplicit.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.HIDE_EXPLICIT) R.color.brand_secondary else R.color.shade_quaternary
        )
        binding.imageHideExplicit.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.HIDE_EXPLICIT) R.color.brand_primary else R.color.shade_secondary
            )
        )

        // Strict Filtering Content
        binding.strictFilteringContent.strokeWidth = if (selected == StrengthLevel.STRICT) 4 else 0
        binding.strictFilteringContent.strokeColor = borderColor
        binding.strictFilteringContent.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.STRICT) R.color.brand_tertiary else R.color.background_bottomSheetCard
        )
        binding.StrictFiltering.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.STRICT) R.color.brand_primary else R.color.shade_primary
            )
        )
        binding.StrictFilteringDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.STRICT) R.color.brand_primary else R.color.shade_secondary
            )
        )
        // Frame & Image
        binding.frameStrictFiltering.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.STRICT) R.color.brand_secondary else R.color.shade_quaternary
        )
        binding.imageStrictFiltering.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.STRICT) R.color.brand_primary else R.color.shade_secondary
            )
        )

        // Show All Content
        binding.showAllContent.strokeWidth = if (selected == StrengthLevel.SHOW_ALL) 4 else 0
        binding.showAllContent.strokeColor = borderColor
        binding.showAllContent.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.SHOW_ALL) R.color.brand_tertiary else R.color.background_bottomSheetCard
        )
        binding.ShowAllContent.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.SHOW_ALL) R.color.brand_primary else R.color.shade_primary
            )
        )
        binding.ShowAllContentDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.SHOW_ALL) R.color.brand_primary else R.color.shade_secondary
            )
        )
        // Frame & Image
        binding.frameShowAll.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (selected == StrengthLevel.SHOW_ALL) R.color.brand_secondary else R.color.shade_quaternary
        )
        binding.imageShowAll.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                if (selected == StrengthLevel.SHOW_ALL) R.color.brand_primary else R.color.shade_secondary
            )
        )
    }


}
