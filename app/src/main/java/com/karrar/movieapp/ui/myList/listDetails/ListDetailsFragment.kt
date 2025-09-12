package com.karrar.movieapp.ui.myList.listDetails

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentListDetailsBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.myList.listDetails.listDetailsUIState.ListDetailsUIEvent
import com.karrar.movieapp.ui.myList.listDetails.listDetailsUIState.SavedMediaUIState
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListDetailsFragment : BaseFragment<FragmentListDetailsBinding>() {

    override val layoutIdFragment = R.layout.fragment_list_details
    override val viewModel: ListDetailsViewModel by viewModels()

    private lateinit var adapter: ListDetailsAdapter
    private var displayedList: List<SavedMediaUIState> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(false, viewModel.args.listName)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        adapter = ListDetailsAdapter(mutableListOf(), viewModel)
        binding.lists.adapter = adapter

        setupSwipeToDelete()
        binding.btnGoToExplore.setOnClickListener {
            findNavController().navigate(R.id.action_listDetailsFragment_to_exploringFragment)
        }
        collectLast(viewModel.listDetailsUIEvent) {
            it.getContentIfNotHandled()?.let { event -> onEvent(event) }
        }

        collectLast(viewModel.listDetailsUIState) {
            displayedList = it.savedMedia
            adapter.setItems(displayedList)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeToDelete() {
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.due_tone_trash)!!
        val bgMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)

        var deleteBounds: Rect? = null
        var swipedPosition = RecyclerView.NO_POSITION
        val openItems = mutableSetOf<Int>()

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val maxSwipe = -itemView.width * 0.20f

                val pos = viewHolder.bindingAdapterPosition
                val isOpen = openItems.contains(pos)

                val clampedDx = when {
                    isOpen && dX > 0 -> 0f
                    dX < 0 -> dX.coerceAtLeast(maxSwipe)
                    else -> dX
                }

                super.onChildDraw(c, recyclerView, viewHolder, clampedDx, dY, actionState, isCurrentlyActive)

                if (clampedDx <= maxSwipe) {
                    drawBackgroundWithMargin(c, itemView, clampedDx, bgMargin)
                    deleteBounds = drawDeleteIcon(c, itemView, deleteIcon, clampedDx, bgMargin)
                    swipedPosition = pos
                } else if (clampedDx == 0f) {
                    openItems.remove(pos)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                val itemView = viewHolder.itemView
                val maxSwipe = -itemView.width * 0.20f
                val currentX = itemView.translationX

                val finalPos = when {
                    currentX < maxSwipe / 2 -> maxSwipe
                    else -> 0f
                }

                itemView.animate()
                    .translationX(finalPos)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        val pos = viewHolder.bindingAdapterPosition
                        if (finalPos == maxSwipe) {
                            openItems.add(pos)
                        } else {
                            openItems.remove(pos)
                        }
                    }
                    .start()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.lists)

        binding.lists.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                deleteBounds?.let { rect ->
                    if (rect.contains(event.x.toInt(), event.y.toInt())) {
                        if (swipedPosition != RecyclerView.NO_POSITION) {
                            val item = displayedList[swipedPosition]
                            viewModel.removeMedia(item)
                            swipedPosition = RecyclerView.NO_POSITION
                            openItems.remove(swipedPosition)
                        }
                    }
                }
            }
            false
        }
    }

    private fun drawBackgroundWithMargin(
        c: Canvas,
        itemView: View,
        dX: Float,
        margin: Int
    ) {
        val context = itemView.context
        val bgColor = ContextCompat.getColor(context, R.color.primary_red)

        val background = GradientDrawable().apply {
            setColor(bgColor)
            cornerRadius = 12f
        }

        background.setBounds(
            itemView.right + dX.toInt() + margin,
            itemView.top + margin *2 ,
            itemView.right - margin,
            itemView.bottom - margin
        )
        background.draw(c)
    }

    private fun drawDeleteIcon(
        c: Canvas,
        itemView: View,
        deleteIcon: Drawable,
        dX: Float,
        margin: Int
    ): Rect {
        DrawableCompat.setTint(
            deleteIcon,
            ContextCompat.getColor(itemView.context, R.color.button_onPrimary)
        )

        val backgroundLeft = itemView.right + dX.toInt() + margin
        val backgroundRight = itemView.right - margin
        val backgroundCenterX = (backgroundLeft + backgroundRight) / 2
        val iconHalfWidth = deleteIcon.intrinsicWidth / 2

        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2 + margin
        val iconBottom = iconTop + deleteIcon.intrinsicHeight
        val iconLeft = backgroundCenterX - iconHalfWidth
        val iconRight = backgroundCenterX + iconHalfWidth

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteIcon.draw(c)

        return Rect(iconLeft, iconTop, iconRight, iconBottom)
    }

    private fun onEvent(event: ListDetailsUIEvent) {
        if (event is ListDetailsUIEvent.OnItemSelected) {
            if (event.savedMediaUIState.mediaType == Constants.MOVIE) {
                navigateToMovieDetails(event.savedMediaUIState.mediaID)
            } else {
                navigateToTvShowDetails(event.savedMediaUIState.mediaID)
            }
        }
    }

    private fun navigateToMovieDetails(id: Int) {
        findNavController().navigate(
            ListDetailsFragmentDirections.actionSavedListFragmentToMovieDetailFragment(id)
        )
    }

    private fun navigateToTvShowDetails(id: Int) {
        findNavController().navigate(
            ListDetailsFragmentDirections.actionListDetailsFragmentToTvShowDetailsFragment(id)
        )
    }
}
