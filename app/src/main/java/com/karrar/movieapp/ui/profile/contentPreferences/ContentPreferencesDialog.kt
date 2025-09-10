package com.karrar.movieapp.ui.profile.contentPreferences

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModel
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.BottomSheetContentPreferencesBinding
import com.karrar.movieapp.ui.base.BaseDialog

class ContentPreferencesDialog() : BaseDialog<BottomSheetContentPreferencesBinding>() {

    override val layoutIdFragment: Int = R.layout.bottom_sheet_content_preferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        binding.hideExplicitContent.setOnClickListener { dismiss() }
        binding.strictFilteringContent.setOnClickListener { dismiss() }
        binding.showAllContent.setOnClickListener { dismiss() }
        binding.iconClose.setOnClickListener { dismiss() }
    }
}