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
    private val listener: MatchQuestionInteractionListener,
    private val onItemSelected: (List<Choice>, MatchQuestionType) -> Unit,
) : BaseAdapter<Choice>(items, listener) {
    override val layoutID: Int
        get() =
            when (type) {
                MatchQuestionType.GENRE -> R.layout.item_wrap_match_choice
                else -> R.layout.item_match_choice
            }
    var selectedItems = mutableListOf<Choice>()

    override fun bind(
        holder: ItemViewHolder,
        position: Int,
    ) {
        val item = items[position]
        val mListener =
            object : ChoicesInteractionListener {
                override fun onChoiceSelected(choice: Choice) {
                    when (type) {
                        MatchQuestionType.TIME, MatchQuestionType.RELEASE -> {
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

    /**
     * Update choices & currently selected items
     */
    fun updateChoices(
        newItems: List<Choice>,
        newSelected: List<Choice>,
    ) {
//        selectedItems = newSelected
//        setItems(newItems)
    }

    interface ChoicesInteractionListener : BaseInteractionListener {
        fun onChoiceSelected(choice: Choice)
    }

    override fun areItemsSame(
        oldItem: Choice,
        newItem: Choice,
    ): Boolean {
        return oldItem.name == newItem.name // assuming Choice has unique id
    }
}
