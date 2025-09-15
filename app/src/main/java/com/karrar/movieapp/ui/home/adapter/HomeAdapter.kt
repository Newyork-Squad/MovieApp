package com.karrar.movieapp.ui.home.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.ItemPopularMovieBinding
import com.karrar.movieapp.domain.enums.AllMediaType
import com.karrar.movieapp.domain.enums.HomeItemsType
import com.karrar.movieapp.ui.adapters.MovieAdapter
import com.karrar.movieapp.ui.adapters.MovieInteractionListener
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.home.HomeInteractionListener
import com.karrar.movieapp.ui.home.HomeItem
import com.karrar.movieapp.ui.models.MediaUiState
import com.karrar.movieapp.ui.myList.CreatedListAdapter
import com.karrar.movieapp.ui.myList.CreatedListInteractionListener
import com.karrar.movieapp.ui.profile.watchhistory.WatchHistoryInteractionListener

class HomeAdapter(
    private var homeItems: MutableList<HomeItem>,
    private val listener: BaseInteractionListener,
) : BaseAdapter<HomeItem>(homeItems, listener) {
    override val layoutID: Int = 0

    fun setItem(item: HomeItem) {
        val newItems = homeItems.apply {
            removeAt(item.priority)
            add(item.priority, item)
        }
        setItems(newItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (homeItems.isNotEmpty())
            bind(holder as ItemViewHolder, position)
    }

    override fun bind(holder: ItemViewHolder, position: Int) {
        if (position != -1)
            when (val currentItem = homeItems[position]) {
                is HomeItem.Slider -> {
                    val adapter =
                        PopularMovieAdapter(currentItem.items, listener as HomeInteractionListener)
                    val viewPager =
                        holder.binding.root.findViewById<ViewPager2>(R.id.viewPagerPopular)
                    viewPager.adapter = adapter

                    setupPageTransformer(viewPager)
                    setupAutoScroll(viewPager, adapter)

                }

                is HomeItem.TopRatedTvShows -> {
                    holder.binding.run {
                        setVariable(
                            BR.adapterRecycler,
                            TVShowAdapter(currentItem.items, listener as TVShowInteractionListener)
                        )
                        setVariable(BR.movieType, currentItem.type)
                        setVariable(BR.mediaType, AllMediaType.TOP_RATED)
                    }
                }

                is HomeItem.RecentlyReleased -> {
                    holder.binding.run {
                        setVariable(
                            BR.adapterRecycler,
                            TVShowAdapter(currentItem.items, listener as TVShowInteractionListener)
                        )
                        setVariable(BR.movieType, currentItem.type)
                        setVariable(BR.mediaType, AllMediaType.LATEST)
                    }
                }

                is HomeItem.Upcoming -> {
                    bindMovie(holder, currentItem.items, currentItem.type)
                }

                is HomeItem.RecentlyViewed -> {
                    holder.binding.run {
                        setVariable(
                            BR.adapterRecycler, RecentlyViewedAdapter(
                                currentItem.items,
                                listener as WatchHistoryInteractionListener
                            )
                        )
                        setVariable(BR.listener, listener as HomeInteractionListener)
                        setVariable(BR.isVisible, currentItem.items.isNotEmpty())
                    }
                }

                is HomeItem.Collections -> {
                    holder.binding.run {
                        setVariable(
                            BR.adapterRecycler, CreatedListAdapter(
                                currentItem.items,
                                listener as CreatedListInteractionListener,
                                isFullWidth = true
                            )
                        )
                        setVariable(BR.listener, listener as HomeInteractionListener)
                        setVariable(BR.isVisible, currentItem.items.isNotEmpty())
                    }
                }
                ////////////////////////
                is HomeItem.WhatShouldWatch->{
                    holder.binding.run {
                        setVariable(BR.listener, listener as HomeInteractionListener)

                    }
                }
                is HomeItem.NeedMoreToWatch->{
                    holder.binding.run {
                        setVariable(BR.listener,listener as HomeInteractionListener)
                    }
                }
            }
    }

    private fun bindMovie(holder: ItemViewHolder, items: List<MediaUiState>, type: HomeItemsType) {
        holder.binding.run {
            setVariable(
                BR.adapterRecycler,
                MovieAdapter(items, listener as MovieInteractionListener)
            )
            setVariable(BR.movieType, type)
        }
    }

    override fun setItems(newItems: List<HomeItem>) {
        homeItems = newItems.sortedBy { it.priority }.toMutableList()
        super.setItems(homeItems)
    }

    override fun areItemsSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        return oldItem.priority == newItem.priority
    }

    override fun areContentSame(
        oldPosition: HomeItem,
        newPosition: HomeItem,
    ): Boolean {
        return oldPosition == newPosition
    }

    override fun getItemViewType(position: Int): Int {
        if (homeItems.isNotEmpty()) {
            return when (homeItems[position]) {
                is HomeItem.Slider -> R.layout.list_popular
                is HomeItem.RecentlyReleased,
                is HomeItem.TopRatedTvShows,
                    -> R.layout.list_tvshow
                is HomeItem.RecentlyViewed -> R.layout.list_recently_viewed
                is HomeItem.Upcoming,
                    -> R.layout.list_movie
                is HomeItem.Collections -> R.layout.list_home_collections
            }
        }
        return -1
    }

    private fun setupPageTransformer(viewPager: ViewPager2) {
        viewPager.offscreenPageLimit = 3
        val sideScale = 1.1f
        val sideTranslationY = 100f
        val sideOffset = -60f

        viewPager.setPageTransformer { page, position ->
            val binding = DataBindingUtil.getBinding<ItemPopularMovieBinding>(page)
            binding?.apply {
                if (position in -0.5f..0.5f) {
                    root.scaleY = 1f
                    root.translationY = 0f
                    root.translationZ = 1f
                    root.translationX = 0f
                    textMovieTitle.visibility = View.VISIBLE
                    textRate.visibility = View.VISIBLE
                    textCategory.visibility = View.VISIBLE
                } else {
                    root.scaleY = sideScale
                    root.translationY = sideTranslationY
                    root.translationZ = 0f
                    root.translationX = position * sideOffset
                    textMovieTitle.visibility = View.GONE
                    textRate.visibility = View.GONE
                    textCategory.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAutoScroll(viewPager: ViewPager2, adapter: PopularMovieAdapter) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val current = viewPager.currentItem
                val next = if (current + 1 < adapter.itemCount) current + 1 else 0
                viewPager.setCurrentItem(next, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)
    }

}