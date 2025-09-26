package com.karrar.movieapp.ui.profile.contentPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.setting.GetContentPreferenceUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveContentPreferenceUseCase
import com.karrar.movieapp.ml.StrengthLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentPreferenceViewModel @Inject constructor(
    private val saveUseCase: SaveContentPreferenceUseCase,
    getUseCase: GetContentPreferenceUseCase
) : ViewModel() {

    private val _selectedPreference = MutableStateFlow(StrengthLevel.HIDE_EXPLICIT)
    val selectedPreference: StateFlow<StrengthLevel> = _selectedPreference

    private val _event = MutableStateFlow<ContentPreferenceEvent?>(null)
    val event: StateFlow<ContentPreferenceEvent?> = _event

    init {
        viewModelScope.launch {
            val saved = getUseCase().first()
            _selectedPreference.value = saved
        }
    }

    fun selectPreference(level: StrengthLevel) {
        viewModelScope.launch {
            saveUseCase(level)
            _selectedPreference.value = level
            _event.update { ContentPreferenceEvent.PreferenceSelected(level) }
        }
    }

    fun closeDialog() {
        _event.update { ContentPreferenceEvent.CloseDialog }
    }
}
