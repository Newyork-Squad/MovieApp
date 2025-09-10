package com.karrar.movieapp.ui.profile.watchhistory

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentWatchHistoryBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchHistoryFragment : BaseFragment<FragmentWatchHistoryBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_watch_history
    override val viewModel: WatchHistoryViewModel by viewModels()
    private lateinit var adapter: WatchHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(true, getString(R.string.watch_history))
        // Initialize the adapter
        adapter = WatchHistoryAdapter(mutableListOf(), viewModel)
        binding.recyclerViewWatchHistory.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { uiState ->
                // Pass the updated list to the adapter
                adapter.setItemList(uiState.allMedia)
            }
        }
        // Set up swipe-to-delete functionality
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                private val limitScrollX = dipToPx(100f, requireContext())
                private var currentScrollX = 0
                private var currentScrollXWhenInActive = 0
                private var initXWhenInActive = 0f
                private var firstInActive = false


                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    // Ensure the list is not empty and position is valid
                    if (position >= 0 && position < adapter.itemCount) {
                        val item = adapter.getItem(position)
                        viewModel.showDeleteButton(position)
                        viewModel.onDeleteClick(item)
                        //adapter.removeItem(position)
                    }
                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {


                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        if (dX == 0f) {
                            currentScrollX = viewHolder.itemView.scrollX
                            firstInActive = true
                        }

                        if (isCurrentlyActive) {
                            var scrollOffset = currentScrollX + (-dX).toInt()
                            scrollOffset = scrollOffset.coerceIn(0, limitScrollX)
                            viewHolder.itemView.scrollTo(scrollOffset, 0)


                        } else {

                            if (firstInActive) {
                                firstInActive = false
                                currentScrollXWhenInActive = viewHolder.itemView.scrollX
                                initXWhenInActive = dX
                            }

                            if (viewHolder.itemView.scrollX < limitScrollX) {
                                viewHolder.itemView.scrollTo(
                                    (currentScrollXWhenInActive * dX / initXWhenInActive).toInt(),
                                    0
                                )
                            }
                        }
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewWatchHistory)

        collectEvent()
    }


    private fun dipToPx(dipValue: Float, context: Context): Int {
        return (dipValue * context.resources.displayMetrics.density).toInt()

    }

    private fun collectEvent() {
        collectLast(viewModel.watchHistoryUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: WatchHistoryUIEvent) {
        val action = when (event) {
            is WatchHistoryUIEvent.MovieEvent -> {
                WatchHistoryFragmentDirections.actionWatchHistoryFragmentToMovieDetailFragment(
                    event.movieID
                )
            }

            is WatchHistoryUIEvent.TVShowEvent -> {
                WatchHistoryFragmentDirections.actionWatchHistoryFragmentToTvShowDetailsFragment(
                    event.tvShowID
                )
            }
        }
        findNavController().navigate(action)
    }

}