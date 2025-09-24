package com.karrar.movieapp.utilities
import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.karrar.movieapp.R
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.ui.base.BaseAdapter
import com.karrar.movieapp.ui.category.uiState.ErrorUIState
import com.karrar.movieapp.ui.category.uiState.GenreUIState
import com.karrar.movieapp.utilities.Constants.FIRST_CATEGORY_ID
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@BindingAdapter("app:showWhenListNotEmpty")
fun <T> showWhenListNotEmpty(view: View, list: List<T>) {
    view.isVisible = list.isNotEmpty() == true
}

@BindingAdapter("app:showWhenListNotEmptyAndHaveThreeItems")
fun <T> showWhenListNotEmptyAndHaveThreeItems(view: View, list: List<T>) {
    view.isVisible = list.isNotEmpty() == true && list.size >= 3
}

@BindingAdapter("app:showWhenListEmpty")
fun <T> showWhenListEmpty(view: View, list: List<T>) {
    view.isVisible = list.isEmpty() == true
}

@BindingAdapter("app:hideWhenListIsEmpty")
fun <T> hideWhenListIsEmpty(view: View, list: List<T>?) {
    if (list?.isEmpty() == true) {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter(value = ["app:error", "app:loading"])
fun <T> showWhenSuccess(view: View, error: List<T>?, loading: Boolean) {
    view.isVisible = error?.isEmpty() == true && !loading
}

@BindingAdapter(value = ["app:noError", "app:doneLoad", "app:emptyData"])
fun <T, M> showWhenNoData(view: View, error: List<T>?, loading: Boolean, data: List<M>?) {
    view.isVisible = error.isNullOrEmpty() && !loading && data.isNullOrEmpty()
}

@BindingAdapter(value = ["app:errorNotEmpty", "app:doneLoading"])
fun <T> hidWhenFail(view: View, error: List<T>?, loading: Boolean) {
    view.visibility = if (!error.isNullOrEmpty() && !loading) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("app:isListEmpty")
fun showWhenDoneLoadingAndListIsEmpty(view: View, emptyList: Boolean) {
    view.isVisible = emptyList
}

@BindingAdapter(value = ["app:showWhenNoInternet"])
fun showWhenNoInternet(view: View, error: List<ErrorUIState>) {
    view.isVisible = !error.none { it.code != ErrorUI.NEED_LOGIN }
}

@BindingAdapter(value = ["app:showWhenNoLogin"])
fun showWhenNoLogin2(view: View, error: List<ErrorUIState>) {
    view.isVisible = !error.none { it.code == ErrorUI.NEED_LOGIN }
}

@BindingAdapter("app:showWhenNoLoggedIn")
fun showWhenNoLoggedIn(view: View, isLoggedIn: Boolean) {
    view.isVisible = !isLoggedIn
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("app:hideIfTrue")
fun hideIfTrue(view: View, value: Boolean) {
    view.isVisible = !value
}

@BindingAdapter("app:isLoggedIn", "app:isFail")
fun showWhenLoggedInAndFail(view: View, isLoggedIn: Boolean, isFail: Boolean) {
    if (isLoggedIn && isFail) {
        view.isVisible = true
    } else if (isLoggedIn) {
        view.isVisible = false
    }
}

@BindingAdapter("isLogged", "isFailure")
fun showWhenIsLoggedInWithoutFail(view: View, isLoggedIn: Boolean, isFail: Boolean) {
    if (isLoggedIn && !isFail) {
        view.isVisible = true
    } else if (isFail) {
        view.isVisible = false
    }
}

//Search
@BindingAdapter("app:showHistory")
fun showHistory(view: View, show: Boolean) {
    view.isVisible = show
}
@BindingAdapter("app:showWhenFocused")
fun showWhenFocused(view: View, isFocused: Boolean) {
    view.isVisible = isFocused
}

@BindingAdapter("app:hideWhenFocused")
fun hideWhenFocused(view: View, isFocused: Boolean) {
    view.isVisible = !isFocused
}

@BindingAdapter(value = ["app:hideWhenBlankSearch"])
fun hideWhenBlankSearch(view: View, text: String) {
    if (text.isBlank()) {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter(value = ["app:isSearchFocused", "app:errorSearch", "app:loadingSearch"])
fun <T> hideWhenSuccessSearch(view: View, isFocused: Boolean, error: List<T>?, loading: Boolean) {
    view.visibility = if (!isFocused && error.isNullOrEmpty() && !loading) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

// different

@BindingAdapter(value = ["app:items", "app:resetScroll"], requireAll = false)
fun <T> setRecyclerItems(view: RecyclerView, items: List<T>?, resetScroll: Boolean? = false) {
    (view.adapter as BaseAdapter<T>?)?.setItems(items ?: emptyList())
    if (resetScroll == true) view.scrollToPosition(0)
}


@BindingAdapter(value = ["app:usePagerSnapHelper"])
fun usePagerSnapHelperWithRecycler(recycler: RecyclerView, useSnapHelper: Boolean = false) {
    if (useSnapHelper)
        PagerSnapHelper().attachToRecyclerView(recycler)
}

@BindingAdapter("app:posterImage")
fun bindMovieImage(image: ImageView, imageURL: String?) {
    imageURL?.let {
        image.load(imageURL) {
            placeholder(R.drawable.loading)
            error(R.drawable.ic_profile_place_holder)
        }
    }
}

@BindingAdapter("app:mediaPoster")
fun loadMediaPoster(image: ImageView, imageURL: String?) {
    imageURL?.let {
        image.load(imageURL) {
            placeholder(R.drawable.loading)
            error(R.drawable.media_place_holder)
        }
    }
}

@BindingAdapter("app:showProfileWhenSuccess")
fun showWhenProfileSuccess(view: View, userName: String) {
    view.isVisible = userName.isNotEmpty()
}

@BindingAdapter("app:overviewText")
fun setOverViewText(view: TextView, text: String) {
    if (text.isNotEmpty()) {
        view.text = text
    } else {
        view.text = view.context.getString(R.string.empty_overview_text)
    }
}

@BindingAdapter("app:setVideoId")
fun setVideoId(view: YouTubePlayerView, videoId: String?) {
    view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            videoId?.let { youTubePlayer.cueVideo(it, 0f) }
        }
    })
}

@BindingAdapter("app:setReleaseDate")
fun setReleaseDate(text: TextView, date: String?) {
    text.text = date?.take(4)
}

@BindingAdapter("app:convertToHoursPattern")
fun convertToHoursPattern(view: TextView, duration: Int) {
    duration.let {
        val hours = (duration / 60).toString()
        val minutes = (duration % 60).toString()
        if (hours == "0") {
            view.text = view.context.getString(R.string.minutes_pattern, minutes)
        } else if (minutes == "0") {
            view.text = view.context.getString(R.string.hours_pattern, hours)
        } else {
            view.text = view.context.getString(R.string.hours_minutes_pattern, hours, minutes)
        }
    }
}

@SuppressLint("StringFormatMatches")
@BindingAdapter(value = ["app:movieHours", "app:movieMinutes"])
fun setDuration(view: TextView, hours: Int?, minutes: Int?) {
    if (hours == 0) {
        view.text = String.format(view.context.getString(R.string.minutes_pattern), minutes)
    } else if (minutes == 0) {
        view.text = String.format(view.context.getString(R.string.hours_pattern), hours)
    } else {
        view.text =
            String.format(view.context.getString(R.string.hours_minutes_pattern), hours, minutes)
    }
}

@BindingAdapter("app:setGenres", "app:listener", "app:selectedChip")
fun <T> setGenresChips(
    view: ChipGroup, chipList: List<GenreUIState>?, listener: T,
    selectedChip: Int?,
) {
    view.removeAllViews()
    chipList?.let {
        it.forEach { genre -> view.addView(view.createChip(genre, listener)) }
    }
    val index = chipList?.indexOf(chipList.find { it.genreID == selectedChip }) ?: FIRST_CATEGORY_ID
    view.getChildAt(index)?.id?.let { view.check(it) }
}

@BindingAdapter("app:genre")
fun setAllGenre(textView: TextView, genreList: List<String>?) {
    genreList?.let {
        textView.text = genreList.joinToString(" . ") { it }
    }
}

@BindingAdapter("app:hideIfNotTypeOfMovie")
fun hideIfNotTypeOfMovie(view: View, mediaType: MediaType?) {
    if (mediaType != MediaType.MOVIE) view.isVisible = false
}

@BindingAdapter("android:rating")
fun setRating(view: RatingBar?, rating: Float) {
    view?.let {
        view.rating = rating
    }
}

@BindingAdapter("showWhenTextNotEmpty")
fun <T> showWhenTextNotEmpty(view: View, text: String) {
    view.isVisible = text.isNotEmpty()
}

@BindingAdapter("icon")
fun setButtonIcon(button: MaterialButton, icon: Int) {
    if (icon != 0) {
        button.icon = ContextCompat.getDrawable(button.context, icon)
    } else {
        button.icon = null
    }
}
@BindingAdapter("app:dynamicShapeAppearance")
fun ShapeableImageView.setDynamicShapeAppearance(isCurrent: Boolean) {
    val styleRes =
        if (isCurrent) {
            R.style.OnboardingImageCornerCurrent
        } else {
            R.style.OnboardingImageCorner
        }
    this.shapeAppearanceModel =
        ShapeAppearanceModel.builder(context, styleRes, styleRes).build()
}

@BindingAdapter("app:hideDividerIfLast")
fun hideDividerIfLast(view: View, isLast: Boolean) {
    view.isVisible = !isLast
}

@SuppressLint("DefaultLocale")
@BindingAdapter("app:setOneDecimalAfterPoint")
fun setOneDecimalAfterPoint(textView: View, value: Float?) {
    value?.let {
        (textView as TextView).text = String.format("%.1f", value)
    }
}


@BindingAdapter("app:cardVisibility")
fun setCardVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:deleteButtonVisibility")
fun setDeleteButtonVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("genresText")
fun TextView.setGenres(genres: List<String>?) {
    text = genres?.joinToString(", ") ?: ""
}


@BindingAdapter("isLanguageSelected")
fun setLanguageSelected(view: View, isSelected: Boolean) {
    if (view is CardView || view is LinearLayout) {
        val color = if (isSelected) {
            ContextCompat.getColor(view.context, R.color.brand_tertiary)
        } else {
            ContextCompat.getColor(view.context, R.color.background_bottomSheetCard)
        }

        view.setBackgroundColor(color)
    }
}


@BindingAdapter(value = ["passwordVisible", "onPasswordToggle"], requireAll = false)
fun setPasswordToggle(
    layout: TextInputLayout,
    isVisible: Boolean,
    onToggle: (() -> Unit)?,
) {
    val editText = layout.editText

    if (isVisible) {
        editText?.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        layout.endIconDrawable = layout.context.getDrawable(R.drawable.outline_eye_opened)
    } else {
        editText?.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.endIconDrawable = layout.context.getDrawable(R.drawable.outline_eye_closed)
    }

    editText?.setSelection(editText.text?.length ?: 0)

    layout.setEndIconOnClickListener { onToggle?.invoke() }
}

@BindingAdapter("app:imageRes")
fun setImageRes(view: ShapeableImageView, resId: Int?) {
    if (resId != null) {
        view.setImageResource(resId)
    } else {
        view.setImageDrawable(null)
    }
}

@BindingAdapter("genresText")
fun TextView.setGenresText(genres: List<String>?) {
    text = genres?.joinToString(", ") ?: ""
}
@BindingAdapter("app:oneDecimal")
fun setOneDecimal(textView: TextView, number: Double?) {
    number?.let {
        textView.text = String.format("%.1f", it)
    }
}
@BindingAdapter("srcCompatSafe")
fun ImageView.setSrcCompatSafe(resId: Int?) {
    if (resId != null && resId != 0) {
        setImageResource(resId)
    } else {
        // show a default image if null
        setImageResource(R.drawable.late_night_thrills)
    }
}


