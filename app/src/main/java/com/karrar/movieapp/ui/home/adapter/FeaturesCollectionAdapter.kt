package com.karrar.movieapp.ui.home.adapter

import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.home.homeUiState.FeaturedCollectionUiState
import com.karrar.movieapp.ui.home.homeUiState.FeaturedCollectionsTarget
import com.karrar.movieapp.R

class FeaturesCollectionAdapter(items:List<FeaturedCollectionUiState>, val listener:FeaturedCollectionListener,

):BaseAdapter<FeaturedCollectionUiState>(items,listener){

    override val layoutID: Int=R.layout.featured_item_card

}
interface FeaturedCollectionListener:BaseInteractionListener{
    fun onClickFeaturedCollections(target: FeaturedCollectionsTarget)

}