package com.karrar.movieapp.ui.myList

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentCreateListDialogBinding
import com.karrar.movieapp.ui.base.BaseDialog
import com.karrar.movieapp.ui.myList.myListUIState.MyListUIEvent
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateListDialog : BaseDialog<FragmentCreateListDialogBinding>() {

    override val layoutIdFragment = R.layout.fragment_create_list_dialog
     val viewModel: MyListsViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        collectLast(viewModel.myListUIEvent) {
            it.peekContent()?.let {
                if (it is MyListUIEvent.CLickAddEvent) {
                    dismissDialog()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismissDialog()
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun dismissDialog() {
        this.dismiss()
    }

}