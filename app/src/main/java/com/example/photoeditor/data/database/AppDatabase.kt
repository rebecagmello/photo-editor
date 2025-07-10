 package com.example.photoeditor.data.database

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import com.example.photoeditor.data.dao.HistoryDao
    import com.example.photoeditor.data.model.ImageHistory

    @Database(entities = [ImageHistory::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun historyDao(): HistoryDao

        companion object {
            @Volatile private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "photo_editor_db"
                    ).build().also { INSTANCE = it }
                }
        }
    }

