package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highscores")
data class Highscore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val score: Int,
    val kills: Int,
    val wave: Int,
    val level: Int,
    val timestamp: Long = System.currentTimeMillis()
)
