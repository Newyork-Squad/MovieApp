package com.karrar.movieapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.base.BasePagingAdapter
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState

class SuggestionsAdapter(
    listener: SuggestionsInteractionListener) :
    BasePagingAdapter<SearchKeywordUIState>(SuggestionsComparator, listener) {

    override val layoutID: Int = R.layout.item_suggestion

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutID,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    object SuggestionsComparator : DiffUtil.ItemCallback<SearchKeywordUIState>() {
        override fun areItemsTheSame(
            oldItem: SearchKeywordUIState,
            newItem: SearchKeywordUIState,
        ): Boolean {
            return oldItem.keyword == newItem.keyword
        }

        override fun areContentsTheSame(
            oldItem: SearchKeywordUIState,
            newItem: SearchKeywordUIState,
        ): Boolean {
            return oldItem == newItem
        }
    }
}


interface SuggestionsInteractionListener : BaseInteractionListener {
    fun onSuggestionsClicked(name: SearchKeywordUIState)
    fun onSuggestionFill(name: SearchKeywordUIState)

}