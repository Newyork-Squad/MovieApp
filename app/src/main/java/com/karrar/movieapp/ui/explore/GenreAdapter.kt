package com.karrar.movieapp.ui.explore

import com.google.android.material.chip.Chip
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.explore.GenresInteractionListener
import com.karrar.movieapp.ui.category.uiState.GenreUIState

class GenreAdapter(
    genres: List<GenreUIState>,
    listener: GenresInteractionListener
) : BaseAdapter<GenreUIState>(genres, listener) {

    override val layoutID: Int = R.layout.item_genre_chip
    private var selectedId: Int = FAKE_GENRE_ID

    fun setSelectedGenre(newSelectedId: Int) {
        if (selectedId == newSelectedId) return
        selectedId = newSelectedId
      notifyDataSetChanged()
    }

    override fun bind(holder: ItemViewHolder, position: Int) {
        super.bind(holder, position)
        val item = getItemAt(position)
        val chip = holder.binding.root.findViewById<Chip>(R.id.chip_genre)
        chip?.isChecked = (item.genreID == selectedId)
    }

    companion object {
        private const val FAKE_GENRE_ID = -1
    }
}