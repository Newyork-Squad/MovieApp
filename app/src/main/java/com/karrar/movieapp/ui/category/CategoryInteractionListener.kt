package com.karrar.movieapp.ui.category

import com.karrar.movieapp.ui.base.BaseInteractionListener

interface CategoryInteractionListener : BaseInteractionListener {
    fun onClickCategory(categoryId: Int)
}