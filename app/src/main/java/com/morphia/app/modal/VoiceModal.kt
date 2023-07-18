package com.morphia.app.modal

import android.speech.tts.Voice

data class VoiceModal(
    val voice: Voice,
    var isSelected: Boolean = false
)
