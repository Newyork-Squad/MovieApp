package com.karrar.movieapp.ui.home.adapter

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.models.MediaUiState

class WhatShouldWatchAdapter (items: List<MediaUiState>, val listener: WhatShouldWatchListener) :
    BaseAdapter<MediaUiState>(items, listener) {
    override val layoutID: Int = R.layout.item_whatshouldwatch
}

interface WhatShouldWatchListener : BaseInteractionListener {
    fun onClickWhatShouldWatch()

}