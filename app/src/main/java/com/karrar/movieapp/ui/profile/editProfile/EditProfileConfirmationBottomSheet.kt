package com.karrar.movieapp.ui.profile.editProfile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.BottomSheetEditProfileConfirmBinding
import com.karrar.movieapp.ui.base.BaseDialog

class EditProfileConfirmDialog : BaseDialog<BottomSheetEditProfileConfirmBinding>() {


    override val layoutIdFragment: Int = R.layout.bottom_sheet_edit_profile_confirm

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnConfirm.setOnClickListener {
            findNavController().navigate(
                R.id.action_editProfileConfirmDialog_to_editProfileFragment
            )
        }
    }
}
