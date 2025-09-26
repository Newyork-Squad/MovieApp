package com.karrar.movieapp.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class SafeImageDetector(
    context: Context,
    private var nsfwThreshold: Float = DEFAULT_THRESHOLD
) {
    private val interpreter: Interpreter
    private val imageProcessor: ImageProcessor

    init {
        val modelBuffer = FileUtil.loadMappedFile(context, "safe_image_model.tflite")
        interpreter = Interpreter(modelBuffer)

        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
    }

    data class DetectionResult(
        val isNSFW: Boolean,
        val safeScore: Float,
        val nsfwScore: Float
    )

    fun analyze(bitmap: Bitmap): DetectionResult {
        Log.d("SafeImageDetector", "bitmap size = ${bitmap.width}x${bitmap.height}")

        var tensorImage = TensorImage(DataType.FLOAT32)

        try {
            val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }

            tensorImage.load(argbBitmap)
        } catch (e: Exception) {
            Log.e("SafeImageDetector", "Error loading bitmap into TensorImage", e)
            return DetectionResult(false, 0f, 0f)
        }

        Log.d("SafeImageDetector", "2")
        tensorImage = imageProcessor.process(tensorImage)
        Log.d("SafeImageDetector", "3")

        val inputBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, INPUT_SIZE, INPUT_SIZE, 3),
            DataType.FLOAT32
        )
        inputBuffer.loadBuffer(tensorImage.buffer)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 2), DataType.FLOAT32)

        try {
            interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())
        } catch (e: Exception) {
            Log.e("SafeImageDetector", "Error running model", e)
        }

        val result = outputBuffer.floatArray
        val safeScore = result.getOrNull(0) ?: 0f
        val nsfwScore = result.getOrNull(1) ?: 0f
        Log.d("SafeImageDetector", "safeScore=$safeScore, nsfwScore=$nsfwScore")

        return DetectionResult(
            isNSFW = nsfwScore > nsfwThreshold,
            safeScore = safeScore,
            nsfwScore = nsfwScore
        )
    }

    companion object {
        private const val INPUT_SIZE = 224
        private const val DEFAULT_THRESHOLD = 0.4f
    }
}