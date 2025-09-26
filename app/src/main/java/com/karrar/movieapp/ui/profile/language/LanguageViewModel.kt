package com.karrar.movieapp.ui.profile.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.ClearAppCacheUseCase
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
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase
) : ViewModel() {

    private val _selectedLanguageCode = MutableStateFlow<String?>(null)
    val selectedLanguageCode: StateFlow<String?> = _selectedLanguageCode.asStateFlow()

    private val _languageUIEvent = MutableStateFlow<Event<LanguageUIEvent?>>(Event(null))
    val languageUIEvent = _languageUIEvent.asStateFlow()

    private val _isLanguageChanging = MutableStateFlow(false)
    val isLanguageChanging: StateFlow<Boolean> = _isLanguageChanging

    init {
        loadCurrentLanguage()
    }

    private fun loadCurrentLanguage() {
        viewModelScope.launch {
            getLanguageUseCase.invoke().collect { code ->
                _selectedLanguageCode.value = code
            }
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            _isLanguageChanging.value = true
            try {
                saveLanguageUseCase(languageCode)
                clearAppCacheUseCase(languageCode)
                _selectedLanguageCode.value = languageCode
                _languageUIEvent.update { Event(LanguageUIEvent.LanguageSelected(languageCode)) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLanguageChanging.value = false
            }
        }
    }

    fun closeDialog() {
        _languageUIEvent.update { Event(LanguageUIEvent.CloseDialogEvent) }
    }
}