package com.karrar.movieapp.ui.search.mediaSearchUIState

data class SearchKeywordUIState(
    val keyword: String,
    val isFromHistory: Boolean = false,
)