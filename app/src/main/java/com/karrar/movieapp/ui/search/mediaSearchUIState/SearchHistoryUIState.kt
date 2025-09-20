package com.karrar.movieapp.ui.search.mediaSearchUIState

import com.karrar.movieapp.data.local.database.entity.SearchHistoryEntity

data class SearchHistoryUIState(
    val name: String ,
    val entity: SearchHistoryEntity
)
