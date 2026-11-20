package com.example.mpaz_pro_2.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AudioHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts
                var result = engine?.setLanguage(Locale.forLanguageTag("es-CL"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = engine?.setLanguage(Locale.forLanguageTag("es-ES"))
                }
                isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}