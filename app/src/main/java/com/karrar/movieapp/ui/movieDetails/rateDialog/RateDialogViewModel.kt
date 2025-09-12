package com.karrar.movieapp.ui.movieDetails.rateDialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.movieDetails.DeleteMovieRatingUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.GetMovieRateUseCase
import com.karrar.movieapp.domain.usecases.tvShowDetails.DeleteSeriesRatingUseCase
import com.karrar.movieapp.domain.usecases.tvShowDetails.GetTvShowDetailsUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.SetRatingUseCase as SetMovieRatingUseCase
import com.karrar.movieapp.domain.usecases.tvShowDetails.SetRatingUesCase as SetTvShowsRatingUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateDialogViewModel @Inject constructor(
    private val getMovieRateUseCase: GetMovieRateUseCase,
    private val getTvShowsRatingUseCase: GetTvShowDetailsUseCase,
    private val setMovieRatingUseCase: SetMovieRatingUseCase,
    private val setTvShowsRatingUseCase: SetTvShowsRatingUseCase,
    private val deleteMovieRatingUseCase: DeleteMovieRatingUseCase,
    private val deleteSeriesRatingUseCase: DeleteSeriesRatingUseCase,
    state: SavedStateHandle,
) : BaseViewModel(), RateDialogInteractionListener {

    // args
    private val args = RateDialogArgs.fromSavedStateHandle(state)


    private val _rateDialogUIState = MutableStateFlow(RateDialogUIState())
    val rateDialogUIState = _rateDialogUIState.asStateFlow()

    private val _rateDialogUIEvent = MutableStateFlow<Event<RateDialogUIEvent?>>(Event(null))
    val rateDialogUIEvent = _rateDialogUIEvent.asStateFlow()

    init {
        getData()
    }

    override fun getData() {
        viewModelScope.launch {
            try {
                val mediaId = args.mediaId
                val mediaType = args.mediaType
                val rate = when (mediaType) {
                    Constants.MOVIE -> {
                        getMovieRateUseCase(mediaId)
                    }

                    Constants.TV_SHOWS -> {
                        getTvShowsRatingUseCase.getTvShowRated(mediaId)
                    }

                    else -> {
                        0.0f
                    }
                }
                _rateDialogUIState.update { it.copy(rate = rate) }
            } catch (t: Throwable) {
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("Error occurred. Please try again.")) }
            }
        }
    }

    override fun onStarClick(newRate: Float) {
        _rateDialogUIState.update { it.copy(rate = newRate) }
    }

    override fun onSubmitClick() {
        val mediaId = args.mediaId
        val mediaType = args.mediaType
        val rate = _rateDialogUIState.value.rate
        if (rate == null) {
            _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("Please select a rating")) }
            return
        }
        viewModelScope.launch {
            try {
                when (mediaType) {
                    Constants.MOVIE -> {
                        setMovieRatingUseCase(mediaId, rate)
                    }

                    Constants.TV_SHOWS -> {
                        setTvShowsRatingUseCase(mediaId, rate)
                    }
                }
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("We submitted your rate")) }
                onCancelClick()
            } catch (e: Throwable) {
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("Error occurred. Please try again.")) }
            }
        }
    }

    override fun onDeleteClick() {
        val mediaId = args.mediaId
        val mediaType = args.mediaType
        viewModelScope.launch {
            try {
                when (mediaType) {
                    Constants.MOVIE -> {
                        deleteMovieRatingUseCase(mediaId)
                    }

                    Constants.TV_SHOWS -> {
                        deleteSeriesRatingUseCase(mediaId)
                    }
                }
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("We removed your rate")) }
                onCancelClick()
            } catch (e: Throwable) {
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("Error occurred. Please try again.")) }
            }
        }
    }


    override fun onCancelClick() {
        viewModelScope.launch {
            _rateDialogUIEvent.update { Event(RateDialogUIEvent.CloseDialog) }
        }
    }
}