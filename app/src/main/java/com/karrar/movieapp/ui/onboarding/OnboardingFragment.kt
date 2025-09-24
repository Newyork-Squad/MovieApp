package com.karrar.movieapp.ui.onboarding

import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentOnboardingBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.ui.onboarding.adapter.OnboardingContentAdapter
import com.karrar.movieapp.ui.onboarding.adapter.OnboardingImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_onboarding
    override val viewModel: OnboardingViewModel by viewModels()
    private lateinit var onboardingImageAdapter: OnboardingImageAdapter

    override fun onStart() {
        super.onStart()
        setTitle(false)
        initViews()
    }

    private fun initViews() {
        val contents =
            listOf(
                OnboardingContent(
                    title = getString(R.string.onboarding_first_page_title),
                    description = getString(R.string.onboarding_first_page_des),
                    imageResId = R.drawable.onboarding_first_image,
                ),
                OnboardingContent(
                    title = getString(R.string.onboarding_second_page_title),
                    description = getString(R.string.onboarding_second_page_des),
                    imageResId = R.drawable.onboarding_second_image,
                ),
                OnboardingContent(
                    title = getString(R.string.onboarding_third_page_title),
                    description = getString(R.string.onboarding_third_page_des),
                    imageResId = R.drawable.onboarding_third_image,
                ),
            )
        initImagePager(contents)
        initContentPager(contents)
        with(binding) {
            nextPage.setOnClickListener(::onNextPageClick)
            goBack.setOnClickListener(::onGoBackClick)
        }
    }

    private fun initContentPager(contents: List<OnboardingContent>) {
        val contentAdapter = OnboardingContentAdapter(contents)
        binding.onboardingRecyclerView.apply {
            adapter = contentAdapter
            isUserInputEnabled = false
        }
    }

    private fun initImagePager(contents: List<OnboardingContent>) {
        onboardingImageAdapter = OnboardingImageAdapter(contents.map { it.imageResId })
        binding.onboardingImagePager.apply {
            adapter = onboardingImageAdapter
            isUserInputEnabled = false
            offscreenPageLimit = 3
            setPageTransformer(RotationPageTransformer())
        }
    }

    private fun onGoBackClick(view: View) {
        with(binding) {
            when (onboardingImagePager.currentItem) {
                1 -> {
                    onboardingImagePager.currentItem = 0
                    onboardingRecyclerView.currentItem = 0
                    onboardingImageAdapter.setCurrentPage(0, 1)
                    goBack.visibility = View.GONE
                }

                2 -> {
                    onboardingImagePager.currentItem = 1
                    onboardingRecyclerView.currentItem = 1
                    onboardingImageAdapter.setCurrentPage(1, 2)
                    finalPageTv.visibility = View.GONE
                }
            }
        }
    }

    private fun onNextPageClick(view: View) {
        with(binding) {
            when (onboardingImagePager.currentItem) {
                0 -> {
                    onboardingImagePager.currentItem = 1
                    onboardingRecyclerView.currentItem = 1
                    onboardingImageAdapter.setCurrentPage(1, 0)
                    goBack.visibility = View.VISIBLE
                }

                1 -> {
                    onboardingImagePager.currentItem = 2
                    onboardingRecyclerView.currentItem = 2
                    onboardingImageAdapter.setCurrentPage(2, 1)
                    finalPageTv.visibility = View.VISIBLE
                }

                2 -> {
                    viewModel.markAsNotFirstLaunch()
                    findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
                }
            }
        }
    }

    inner class RotationPageTransformer : ViewPager2.PageTransformer {
        private val maxRotation = 15f
        private val minScale = 1.1f

        override fun transformPage(page: View, position: Float) {
            val isRTL = ViewCompat.getLayoutDirection(page) == ViewCompat.LAYOUT_DIRECTION_RTL
            val directionFactor = if (isRTL) -1 else 1
            page.apply {
                when {
                    position < -1 -> {
                        rotation = -maxRotation * directionFactor
                        scaleX = minScale + (1 - minScale) * (1 + position)
                        scaleY = minScale + (1 - minScale) * (1 + position)
                    }
                    position <= 0 -> {
                        rotation = position * maxRotation * directionFactor
                        scaleX = minScale + (1 - minScale) * (1 + position)
                        scaleY = minScale + (1 - minScale) * (1 + position)
                    }
                    position <= 1 -> {
                        rotation = position * maxRotation * directionFactor
                        scaleX = minScale + (1 - minScale) * (1 - position)
                        scaleY = minScale + (1 - minScale) * (1 - position)
                    }
                    else -> {
                        rotation = maxRotation * directionFactor
                        scaleX = minScale + (1 - minScale) * (1 + position)
                        scaleY = minScale + (1 - minScale) * (1 + position)
                    }
                }
            }
        }
    }
}
