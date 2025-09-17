package com.karrar.movieapp.ui.main

import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.ClearAppCacheUseCase
import com.karrar.movieapp.domain.usecases.setting.GetDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.GetLanguageUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveLanguageUseCase
import com.karrar.movieapp.domain.usecases.startUp.GetStartUpStateUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStartUpStateUseCase: GetStartUpStateUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val saveDarkModeUseCase: SaveDarkModeUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
) : BaseViewModel(){

    private val _mainUiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.asStateFlow()

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    // Event للتنبيه بتحديث البيانات
    private val _dataRefreshEvent = MutableStateFlow(false)
    val dataRefreshEvent: StateFlow<Boolean> = _dataRefreshEvent

    override fun getData() {
        _mainUiState.update {
            it.copy(
                isFirstLaunch = getStartUpStateUseCase(),
            )
        }

        viewModelScope.launch {
            getLanguageUseCase().collect { lang ->
                _language.value = lang
            }
        }

        viewModelScope.launch {
            getDarkModeUseCase().collect { dark ->
                _darkMode.value = dark
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            getData()
            // إطلاق event لتحديث البيانات في كل الـ ViewModels
            _dataRefreshEvent.value = !_dataRefreshEvent.value
        }
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch {
            saveLanguageUseCase(language)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            saveDarkModeUseCase(enabled)
        }
    }

    suspend fun clearCache(language: String) {
        try {
            clearAppCacheUseCase(language)
            // انتظار إضافي للتأكد من مسح الكاش
            kotlinx.coroutines.delay(200)
        } catch (e: Exception) {
            e.printStackTrace()
            // في حالة فشل مسح الكاش، المتابعة بدونه
        }
    }
}
