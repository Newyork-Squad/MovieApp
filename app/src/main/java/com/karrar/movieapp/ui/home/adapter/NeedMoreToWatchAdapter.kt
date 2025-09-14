package com.karrar.movieapp.ui.home.adapter

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.models.MediaUiState

class NeedMoreToWatchAdapter (items: List<MediaUiState>, val listener: NeedMoreToWatchListener) :
    BaseAdapter<MediaUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_needmoretowatch
}


interface NeedMoreToWatchListener : BaseInteractionListener {
    fun onClickNeedMoreToWatch()

}