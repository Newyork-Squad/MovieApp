package com.karrar.movieapp.ui.home

import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.home.homeUiState.FeaturedCollectionsTarget

interface HomeInteractionListener : BaseInteractionListener {
    fun onClickSeeAllActors()
    fun onClickSeeAllRecentlyViewed()
    fun onClickFeaturedCollections(target: FeaturedCollectionsTarget)
    fun onClickSeeAllCollections()
    fun onClickNeedMoreToWatch()
    fun onClickWhatShouldWatch()

}