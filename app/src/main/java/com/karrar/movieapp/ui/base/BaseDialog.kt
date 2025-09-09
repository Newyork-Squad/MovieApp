package com.karrar.movieapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.lifecycle.ViewModel
import com.karrar.movieapp.BR

abstract class BaseDialog<VDB : ViewDataBinding> : BottomSheetDialogFragment() {
    abstract val layoutIdFragment: Int
    protected lateinit var binding: VDB
    open val bottomPaddingPx: Int = 16

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutIdFragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.setCanceledOnTouchOutside(true)
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                sheet.setPadding(sheet.paddingLeft, sheet.paddingTop, sheet.paddingRight, bottomPaddingPx)
                sheet.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                sheet.requestLayout()
            }
        }
    }
}

