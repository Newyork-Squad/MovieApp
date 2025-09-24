package com.karrar.movieapp.ui.match.result

import com.karrar.movieapp.R
import com.karrar.movieapp.domain.enums.HomeItemsType
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.match.questions.MatchQuestionUiState

class MoviePagerAdapter(
    items: List<String>,
    listener: BaseInteractionListener
) : BaseAdapter<String>(items, listener) {
    override val layoutID: Int = R.layout.item_movie_pager
}
