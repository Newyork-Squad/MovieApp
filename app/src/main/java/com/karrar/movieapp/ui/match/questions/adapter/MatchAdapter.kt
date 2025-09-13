package com.karrar.movieapp.ui.match.questions.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.karrar.movieapp.BR
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.match.questions.Choice
import com.karrar.movieapp.ui.match.questions.MatchQuestion
import com.karrar.movieapp.ui.match.questions.MatchQuestionInteractionListener
import com.karrar.movieapp.ui.match.questions.MatchQuestionType

class MatchAdapter(
    private var items: MutableList<MatchQuestion>,
    private val listener: MatchQuestionInteractionListener,
    private val onSelect: (List<Choice>, MatchQuestionType) -> Unit,
) : BaseAdapter<MatchQuestion>(items, listener) {
    override val layoutID: Int = R.layout.item_match_question

    override fun bind(
        holder: ItemViewHolder,
        position: Int,
    ) {
        val choicesAdapters = mutableMapOf<Int, ChoicesAdapter>()
        val item = items[position]

        val choicesAdapter =
            choicesAdapters.getOrPut(position) {
                ChoicesAdapter(item.choices, item.type, listener, onSelect)
            }

        val layoutManager =
            when (item.type) {
                MatchQuestionType.MOOD -> GridLayoutManager(holder.itemView.context, 2)
                MatchQuestionType.GENRE ->
                    FlexboxLayoutManager(holder.itemView.context).apply {
                        flexDirection = FlexDirection.ROW
                        justifyContent = JustifyContent.FLEX_START
                    }

                MatchQuestionType.MEDIA_RUNTIME -> LinearLayoutManager(holder.itemView.context)
                MatchQuestionType.TIME_PERIOD -> GridLayoutManager(holder.itemView.context, 3)
            }

        holder.binding.apply {
            setVariable(BR.question, item.question)
            setVariable(BR.layoutManager, layoutManager)
            setVariable(BR.adapter, choicesAdapter)
            setVariable(BR.isCurrent, item.type == listener.getCurrentQuestionType())
            executePendingBindings()
        }
    }

    fun emitItems(items: List<MatchQuestion>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}
