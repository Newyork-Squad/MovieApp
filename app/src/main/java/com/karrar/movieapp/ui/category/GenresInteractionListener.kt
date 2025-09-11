package com.karrar.movieapp.ui.category

import com.karrar.movieapp.ui.base.BaseInteractionListener

interface GenresInteractionListener : BaseInteractionListener {
    fun onGenreSelected(genreId: Int)
}