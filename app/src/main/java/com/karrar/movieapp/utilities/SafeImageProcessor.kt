package com.karrar.movieapp.utilities

import android.content.Context
import android.graphics.Bitmap
import com.karrar.movieapp.ml.SafeImageDetector


object SafeImageProcessor {

    suspend fun processImage(
        context: Context,
        bitmap: Bitmap,
        blurRadius: Int = 20
    ): ProcessedResult {

        val detector = SafeImageDetector(context)
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



