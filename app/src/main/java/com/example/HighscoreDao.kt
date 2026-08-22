package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighscoreDao {
    @Query("SELECT * FROM highscores ORDER BY score DESC LIMIT 10")
    fun getTop10(): Flow<List<Highscore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Highscore)

    @Query("DELETE FROM highscores")
    suspend fun deleteAll()
}
