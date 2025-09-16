package com.karrar.movieapp.ui.search.adapters

import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.SuggestionUiState

class SuggestionsAdapter(
    items: List<SuggestionUiState>,
    val layoutId: Int,
    val listener: SuggestionsInteractionListener,
) : BaseAdapter<SuggestionUiState>(items, listener) {
    override val layoutID: Int = layoutId

    override fun areItemsSame(oldItem: SuggestionUiState, newItem: SuggestionUiState): Boolean {
        return oldItem.id == newItem.id
    }
}

interface SuggestionsInteractionListener : BaseInteractionListener {
    fun onSuggestionsClicked(name: String)
    fun onSuggestionFill(name: String)

}