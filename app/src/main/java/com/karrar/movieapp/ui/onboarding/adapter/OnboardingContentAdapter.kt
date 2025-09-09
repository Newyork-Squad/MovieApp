package com.karrar.movieapp.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.databinding.ItemOnboardingContentMessagesBinding
import com.karrar.movieapp.ui.onboarding.OnboardingContent

class OnboardingContentAdapter(
    private val items: List<OnboardingContent>,
) : RecyclerView.Adapter<OnboardingContentAdapter.ContentViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContentViewHolder {
        val binding =
            ItemOnboardingContentMessagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ContentViewHolder,
        position: Int,
    ) {
        val content = items[position]
        holder.bind(content)
    }

    override fun getItemCount(): Int = items.size

    class ContentViewHolder(
        private val binding: ItemOnboardingContentMessagesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: OnboardingContent) {
            binding.title.text = content.title
            binding.description.text = content.description
        }
    }
}
