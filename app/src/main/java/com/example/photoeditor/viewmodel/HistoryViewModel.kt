package com.example.photoeditor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.photoeditor.data.database.AppDatabase
import com.example.photoeditor.data.model.ImageHistory
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).historyDao()

    val history = dao.getAll().asLiveData()

    fun addHistory(uri: String, filename: String) {
        viewModelScope.launch {
            dao.insert(ImageHistory(imageUri = uri, filename = filename, timestamp = System.currentTimeMillis()))
        }
    }
}
