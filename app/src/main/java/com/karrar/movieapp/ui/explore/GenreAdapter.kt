package com.karrar.movieapp.ui.explore

import android.util.Log
import androidx.databinding.library.baseAdapters.BR.listener
import com.google.android.material.chip.Chip
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.category.CategoryInteractionListener
import com.karrar.movieapp.ui.category.uiState.GenreUIState

class GenreAdapter(
    genres: List<GenreUIState>,
    listener: CategoryInteractionListener
) : BaseAdapter<GenreUIState>(genres, listener) {

    override val layoutID: Int = R.layout.item_genre_chip

    private var selectedId: Int = -1


    fun setSelectedGenre(id: Int) {
        if (selectedId == id) return
        selectedId = id
        notifyDataSetChanged()
    }

    override fun bind(holder: ItemViewHolder, position: Int) {
        super.bind(holder, position)
0
        val item = getItemAt(position)
        val chip = holder.binding.root.findViewById<Chip>(R.id.chip_genre)

        chip?.isChecked = (item.genreID == selectedId)

    }

}