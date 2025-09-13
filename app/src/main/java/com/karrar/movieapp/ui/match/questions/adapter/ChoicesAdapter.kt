package com.karrar.movieapp.ui.match.questions.adapter

import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.base.BaseInteractionListener
import com.karrar.movieapp.ui.match.questions.Choice
import com.karrar.movieapp.ui.match.questions.MatchQuestionInteractionListener
import com.karrar.movieapp.ui.match.questions.MatchQuestionType

class ChoicesAdapter(
    private val items: List<Choice>,
    private val type: MatchQuestionType,
    listener: MatchQuestionInteractionListener,
    private val onItemSelected: (List<Choice>, MatchQuestionType) -> Unit,
) : BaseAdapter<Choice>(items, listener) {
    override val layoutID: Int
        get() =
            when (type) {
                MatchQuestionType.GENRE -> R.layout.item_wrap_match_choice
                else -> R.layout.item_match_choice
            }
    private var selectedItems = mutableListOf<Choice>()

    override fun bind(
        holder: ItemViewHolder,
        position: Int,
    ) {
        val item = items[position]
        val mListener =
            object : ChoicesInteractionListener {
                override fun onChoiceSelected(choice: Choice) {
                    when (type) {
                        MatchQuestionType.MEDIA_RUNTIME, MatchQuestionType.TIME_PERIOD -> {
                            if (selectedItems.isNotEmpty()) {
                                val previousSelectedIndex = selectedItems.first()
                                selectedItems.clear()
                                notifyItemChanged(items.indexOf(previousSelectedIndex))
                            }
                            selectedItems.add(choice)
                            notifyItemChanged(items.indexOf(choice))
                        }
                        else -> {
                            if (selectedItems.contains(choice)) {
                                selectedItems.remove(choice)
                            } else {
                                selectedItems.add(choice)
                            }
                            notifyItemChanged(items.indexOf(choice))
                        }
                    }
                    onItemSelected(selectedItems, type)
                }
            }

        holder.binding.apply {
            setVariable(BR.choice, item)
            setVariable(BR.listener, mListener)
            setVariable(BR.isSelected, selectedItems.contains(item))
            executePendingBindings()
        }
    }

    interface ChoicesInteractionListener : BaseInteractionListener {
        fun onChoiceSelected(choice: Choice)
    }
}
