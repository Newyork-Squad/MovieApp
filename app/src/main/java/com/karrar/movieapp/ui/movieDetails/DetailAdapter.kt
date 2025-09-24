package com.karrar.movieapp.ui.movieDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.adapters.ActorAdapter
import com.karrar.movieapp.ui.adapters.ActorsInteractionListener
import com.karrar.movieapp.ui.adapters.CrewAdapter
import com.karrar.movieapp.ui.adapters.MovieAdapter
import com.karrar.movieapp.ui.adapters.MovieInteractionListener
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.movieDetails.movieDetailsUIState.DetailItemUIState

class DetailAdapter(
    private var items: List<DetailItemUIState>,
    private val listener: BaseInteractionListener,
) : BaseAdapter<DetailItemUIState>(items, listener) {
    override val layoutID: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder as ItemViewHolder, position)
    }

    override fun bind(holder: ItemViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is DetailItemUIState.Header -> {
                holder.binding.run {
                    setVariable(BR.item, currentItem.data)
                    setVariable(BR.listener, listener as DetailInteractionListener)
                }
            }

            is DetailItemUIState.Cast -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        ActorAdapter(
                            currentItem.data,
                            R.layout.item_cast,
                            listener as ActorsInteractionListener
                        )
                    )
                    val recyclerView =
                        root.findViewById<RecyclerView>(R.id.cast_adapter)
                    val layoutManager =
                        GridLayoutManager(root.context, 2, GridLayoutManager.HORIZONTAL, false)
                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (currentItem.data.size == 1) 2 else 1
                        }
                    }
                    recyclerView.layoutManager = layoutManager
                }
            }

            is DetailItemUIState.Crew -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        CrewAdapter(currentItem.data, R.layout.item_crew, listener)
                    )
                }
            }

            is DetailItemUIState.SimilarMovies -> {
                holder.binding.run {
                    setVariable(
                        BR.adapterRecycler,
                        MovieAdapter(currentItem.data, listener as MovieInteractionListener)
                    )
                }
            }

            is DetailItemUIState.Rating -> {
                holder.binding.run {
                    setVariable(BR.rate, currentItem.rate)
                    setVariable(BR.viewModel, currentItem.viewModel)
                }
            }

            is DetailItemUIState.Comment -> {
                holder.binding.run {
                    setVariable(BR.item, currentItem.data)
                    setVariable(BR.listener, listener)
                }
            }

            is DetailItemUIState.ReviewText -> {
                holder.binding.run {
                    setVariable(BR.listener,listener)
                }
            }
        }
    }

    override fun setItems(newItems: List<DetailItemUIState>) {
        items = newItems.sortedBy { it.priority }
        super.setItems(items)
    }

    override fun areItemsSame(oldItem: DetailItemUIState, newItem: DetailItemUIState): Boolean {
        return oldItem.priority == newItem.priority
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DetailItemUIState.Header -> R.layout.item_movie_detail_header
            is DetailItemUIState.Cast -> R.layout.list_cast
            is DetailItemUIState.Crew -> R.layout.list_crew
            is DetailItemUIState.SimilarMovies -> R.layout.list_similar_movie
            is DetailItemUIState.Rating -> R.layout.item_rating
            is DetailItemUIState.Comment -> R.layout.item_top_review
            is DetailItemUIState.ReviewText -> R.layout.item_review_text
        }
    }

}


