package com.karrar.movieapp.ui.explore

import com.karrar.movieapp.ui.base.BaseInteractionListener

interface GenresInteractionListener : BaseInteractionListener {
    fun onGenreSelected(genreId: Int)
}