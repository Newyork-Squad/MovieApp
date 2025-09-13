package com.karrar.movieapp.ui.myList

import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.myList.myListUIState.CreatedListUIState

class CreatedListAdapter(
    items: List<CreatedListUIState>,
    listener: CreatedListInteractionListener,
    private val isFullWidth: Boolean = false
) : BaseAdapter<CreatedListUIState>(items, listener) {
    override val layoutID: Int = R.layout.item_saved_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.card_view)
        val layoutParams = cardView.layoutParams

        if (isFullWidth) {
            layoutParams.width =
                holder.itemView.context.resources.getDimensionPixelSize(R.dimen.card_width_small)
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        cardView.layoutParams = layoutParams
    }
}

interface CreatedListInteractionListener : BaseInteractionListener {
    fun onListClick(item: CreatedListUIState)
}