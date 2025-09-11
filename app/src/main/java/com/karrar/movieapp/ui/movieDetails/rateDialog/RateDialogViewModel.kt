package com.karrar.movieapp.ui.movieDetails.rateDialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.GetSessionIDUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.GetMovieRateUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.SetRatingUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.movieDetails.MovieDetailsUIEvent
import com.karrar.movieapp.ui.movieDetails.saveMovie.SaveMovieDialogArgs
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
    private val setRatingUseCase: SetRatingUseCase,
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
                val rate = getMovieRateUseCase(mediaId)
                _rateDialogUIState.update { it.copy(rate = rate) }
            } catch (t: Throwable) {
            }
        }
    }

    override fun onStarClick(newRate: Float) {
        _rateDialogUIState.update { it.copy(rate = newRate) }
    }

    override fun onSubmitClick() {
        val mediaId = args.mediaId
        val rate = _rateDialogUIState.value.rate
        if (rate == null) {
            _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("Please select a rating")) }
            return
        }
        viewModelScope.launch {
            try {
                setRatingUseCase(mediaId, rate)
                _rateDialogUIEvent.update { Event(RateDialogUIEvent.ShowMessage("We submitted your rate")) }
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