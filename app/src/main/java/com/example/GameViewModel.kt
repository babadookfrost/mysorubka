package com.example

import android.app.Application
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.highscoreDao()

    val topHighscores: StateFlow<List<Highscore>> = dao.getTop10()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun saveHighscore(name: String, score: Int, kills: Int, wave: Int, level: Int) {
        viewModelScope.launch {
            dao.insert(Highscore(name = name, score = score, kills = kills, wave = wave, level = level))
        }
    }

    // Sound system (placeholder - generates simple beeps)
    private var soundPool: SoundPool? = null
    private var initialized = false

    fun initSound() {
        if (initialized) return
        try {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder().setMaxStreams(8).setAudioAttributes(attrs).build()
            initialized = true
        } catch (_: Exception) { }
    }

    fun playSound(type: String) {
        // Placeholder: in production, load real .ogg files
        // soundPool?.play(loadedSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() { soundPool?.release(); soundPool = null }
}
