package com.karrar.movieapp.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karrar.movieapp.databinding.ItemOnboardingImagePageBinding

class OnboardingImageAdapter(
    private val items: List<Int>,
) : RecyclerView.Adapter<OnboardingImageAdapter.PageViewHolder>() {
    private var currentPage: Int = 0

    class PageViewHolder(
        val binding: ItemOnboardingImagePageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Int,
            position: Int,
            currentPage: Int,
        ) {
            binding.isCurrent = currentPage == position
            binding.pagerImage.setImageResource(item)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PageViewHolder {
        val binding =
            ItemOnboardingImagePageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PageViewHolder,
        position: Int,
    ) {
        holder.bind(items[position], position, currentPage)
    }

    override fun getItemCount(): Int = items.size

    fun setCurrentPage(
        currentPosition: Int,
        previousPosition: Int,
    ) {
        currentPage = currentPosition
        notifyItemChanged(currentPosition)
        notifyItemChanged(previousPosition)
    }
}
