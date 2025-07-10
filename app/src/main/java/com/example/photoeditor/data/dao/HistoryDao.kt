package com.example.photoeditor.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.photoeditor.data.model.ImageHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: ImageHistory)

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ImageHistory>>
}
