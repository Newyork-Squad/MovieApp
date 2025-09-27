package com.karrar.movieapp.utilities

import android.content.Context
import android.graphics.Bitmap
import com.karrar.movieapp.domain.usecases.setting.GetContentPreferenceUseCase
import com.karrar.movieapp.ml.SafeImageDetector
import com.karrar.movieapp.ml.StrengthLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafeImageProcessor @Inject constructor(
    private val getContentPreferenceUseCase: GetContentPreferenceUseCase
) {
    private val _currentStrengthLevel = MutableStateFlow<StrengthLevel?>(null)
    val currentStrengthLevel: StateFlow<StrengthLevel?> get() = _currentStrengthLevel

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getContentPreferenceUseCase().collect { level ->
                _currentStrengthLevel.value = level
            }
        }
    }

    suspend fun processImage(
        context: Context,
        bitmap: Bitmap,
        blurRadius: Int = 20
    ): ProcessedResult {
        val strengthLevel = _currentStrengthLevel.value ?: StrengthLevel.HIDE_EXPLICIT

        val threshold = when (strengthLevel) {
            StrengthLevel.SHOW_ALL -> 1f
            StrengthLevel.HIDE_EXPLICIT -> 0.4f
            StrengthLevel.STRICT -> 0.2f
        }

        val detector = SafeImageDetector(context, threshold)
        val result = detector.analyze(bitmap)

        val finalBitmap = if (result.isNSFW) Blur.blur(context, bitmap, blurRadius) else bitmap

        return ProcessedResult(finalBitmap, result.isNSFW, result.nsfwScore)
    }

    data class ProcessedResult(
        val finalBitmap: Bitmap,
        val wasBlurred: Boolean,
        val nsfwScore: Float
    )
}



