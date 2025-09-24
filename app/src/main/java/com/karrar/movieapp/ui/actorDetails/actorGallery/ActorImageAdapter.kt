package com.karrar.movieapp.ui.actorDetails.actorGallery

import android.view.View
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener

class ActorImageAdapter(
    val items: List<GalleryItem>,
) : BaseAdapter<GalleryItem>(items, object : BaseInteractionListener {}) {
    override val layoutID: Int
        get() = R.layout.item_actor_image

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        (holder as ItemViewHolder)
        if (position % 2 == 0) {
            holder.binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR
        } else {
            holder.binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}
