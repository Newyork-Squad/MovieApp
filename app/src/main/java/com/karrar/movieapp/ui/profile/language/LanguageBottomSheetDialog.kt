package com.karrar.movieapp.ui.profile.language

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.BottomSheetLanguageBinding
import com.karrar.movieapp.ui.base.BaseDialog
import com.karrar.movieapp.ui.main.MainActivity
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setWidthPercent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguagePickerDialog : BaseDialog<BottomSheetLanguageBinding>() {

    override val layoutIdFragment: Int = R.layout.bottom_sheet_language
    private val viewModel: LanguageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setWidthPercent(90)

        collectLast(viewModel.selectedLanguage) { language ->
            updateCardSelection(language)
        }

        binding.cardEnglish.setOnClickListener {
            viewModel.selectLanguage("English")
        }

        binding.cardArabic.setOnClickListener {
            viewModel.selectLanguage("Arabic")
        }

        collectLast(viewModel.languageUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun updateCardSelection(selected: String?) {
        val borderColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)

        binding.cardEnglish.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                if (selected == "English") R.color.brand_tertiary else R.color.background_bottomSheetCard
            )

        binding.cardEnglish.strokeWidth = if (selected == "English") 4 else 0
        binding.cardEnglish.strokeColor = borderColor
        binding.textEnglish.setTextColor(
            if (selected == "English")
                ContextCompat.getColor(requireContext(), R.color.brand_primary)
            else
                ContextCompat.getColor(requireContext(), R.color.shade_primary)
        )

        binding.cardArabic.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                if (selected == "Arabic") R.color.brand_tertiary else R.color.background_bottomSheetCard
            )

        binding.cardArabic.strokeWidth = if (selected == "Arabic") 4 else 0
        binding.cardArabic.strokeColor = borderColor
        binding.textArabic.setTextColor(
            if (selected == "Arabic")
                ContextCompat.getColor(requireContext(), R.color.brand_primary)
            else
                ContextCompat.getColor(requireContext(), R.color.shade_primary)
        )
    }

    private fun onEvent(event: LanguageUIEvent) {
        when (event) {
            is LanguageUIEvent.LanguageSelected -> {
                dismiss()

                (requireActivity() as? MainActivity)?.let { activity ->
                    activity.lifecycleScope.launch {
                        try {
                            activity.updateLocale(event.language)
                            activity.recreate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            activity.recreate()
                        }
                    }
                }
            }
            LanguageUIEvent.CloseDialogEvent -> dismiss()
        }
    }
}
