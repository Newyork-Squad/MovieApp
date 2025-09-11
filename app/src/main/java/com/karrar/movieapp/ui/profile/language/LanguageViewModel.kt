package com.karrar.movieapp.ui.profile.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.setting.GetLanguageUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveLanguageUseCase
import com.karrar.movieapp.utilities.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow<String?>(null)
    val selectedLanguage: StateFlow<String?> = _selectedLanguage.asStateFlow()

    private val _languageUIEvent = MutableStateFlow<Event<LanguageUIEvent?>>(Event(null))
    val languageUIEvent = _languageUIEvent.asStateFlow()

    init {
        loadCurrentLanguage()
    }

    private fun loadCurrentLanguage() {
        viewModelScope.launch {
            getLanguageUseCase().collect { language ->
                _selectedLanguage.value = language
            }
        }
    }


    fun selectLanguage(language: String) {
        viewModelScope.launch {
            saveLanguageUseCase(language)
            _selectedLanguage.value = language
            _languageUIEvent.update { Event(LanguageUIEvent.LanguageSelected(language)) }
        }
    }

    fun closeDialog() {
        _languageUIEvent.update { Event(LanguageUIEvent.CloseDialogEvent) }
    }
}