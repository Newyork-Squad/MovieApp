package com.karrar.movieapp.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.karrar.movieapp.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class MicEvent {
    object ReadyForSpeech : MicEvent()
    data class PartialResult(val text: String) : MicEvent()
    data class Result(val text: String) : MicEvent()
    data class Error(val message: String) : MicEvent()
}

class MicControllerHelper(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
) : DefaultLifecycleObserver {

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null

    private val _events = MutableSharedFlow<MicEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<MicEvent> = _events

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun initRecognizer() {
        if (!isAvailable()) {
            _events.tryEmit(MicEvent.Error(context.getString(R.string.speech_recognition_not_available_on_this_device)))
            return
        }

        if (speechRecognizer != null) return

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(innerRecognitionListener)
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    fun startListening(): Boolean {
        if (!isAvailable()) {
            _events.tryEmit(MicEvent.Error(context.getString(R.string.speech_recognition_is_not_available)))
            return false
        }

        if (speechRecognizer == null || recognizerIntent == null) initRecognizer()

        return try {
            speechRecognizer?.startListening(recognizerIntent)
            true
        } catch (t: Throwable) {
            _events.tryEmit(MicEvent.Error(
                context.getString(
                    R.string.cannot_start_speech_recognizer,
                    t.message ?: "unknown"
                )))
            false
        }
    }


    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        recognizerIntent = null
    }

    private val innerRecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _events.tryEmit(MicEvent.ReadyForSpeech)
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            val msg = getSpeechErrorMessage(context, error)
            _events.tryEmit(MicEvent.Error(msg))
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                _events.tryEmit(MicEvent.Result(matches[0]))
            } else {
                _events.tryEmit(MicEvent.Error(context.getString(R.string.no_results)))
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!partial.isNullOrEmpty()) _events.tryEmit(MicEvent.PartialResult(partial[0]))
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun buildVoiceIntent(prompt: String? = null): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            prompt?.let { putExtra(RecognizerIntent.EXTRA_PROMPT, it) }
        }

    private fun getSpeechErrorMessage(context: Context, error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_NETWORK -> context.getString(R.string.speech_error_network)
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> context.getString(R.string.speech_error_timeout)
            SpeechRecognizer.ERROR_NO_MATCH -> context.getString(R.string.speech_error_no_match)
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> context.getString(R.string.speech_error_busy)
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> context.getString(R.string.speech_error_permissions)
            else -> context.getString(R.string.speech_error_unknown)
        }
    }

}