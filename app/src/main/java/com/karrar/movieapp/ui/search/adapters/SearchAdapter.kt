package com.karrar.movieapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchItemUiState

class SearchAdapter(
    private var items: List<SearchItemUiState>,
    private val listener: SearchItemInteractionListener,
) : BaseAdapter<SearchItemUiState>(items, listener) {
    override val layoutID: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder as ItemViewHolder, position)
    }

    override fun bind(holder: ItemViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is SearchItemUiState.SearchItemHistory -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        SearchHistoryAdapter(
                            currentItem.data,
                            R.layout.item_search_history,
                            listener as SearchHistoryInteractionListener
                        )
                    )
                    setVariable(BR.listener, listener)
                    executePendingBindings()
                }

            }

            is SearchItemUiState.RecentViewed -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        RecentViewAdapter(
                            currentItem.data,
                            R.layout.item_recent_viewed,
                            listener as RecentViewedInteractionListener
                        )
                    )
                    setVariable(BR.listener, listener)
                    executePendingBindings()
                }
            }

            is SearchItemUiState.SuggestionsItems -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        SuggestionsAdapter(
                            currentItem.data,
                            R.layout.item_suggestion,
                            listener as SuggestionsInteractionListener
                        )
                    )
                    setVariable(BR.listener,listener)
                    executePendingBindings()
                }
            }
        }
    }

    override fun setItems(newItems: List<SearchItemUiState>) {
        items = newItems.sortedBy { it.priority }
        super.setItems(items)
    }

    override fun areItemsSame(oldItem: SearchItemUiState, newItem: SearchItemUiState): Boolean {
        return oldItem.priority == newItem.priority
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SearchItemUiState.SearchItemHistory -> R.layout.list_search_history
            is SearchItemUiState.RecentViewed -> R.layout.list_recent_viewed
            is SearchItemUiState.SuggestionsItems -> R.layout.list_suggest_search
        }
    }

}

interface SearchItemInteractionListener : BaseInteractionListener {
    fun onClearAllClicked()
}
