package com.example.photoeditor

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LightViewModel : ViewModel() {
        private val _image: MutableLiveData<Bitmap> = MutableLiveData()
        val image: LiveData<Bitmap> = _image

        fun changeImage(newImage: Bitmap) {
            _image.postValue(newImage)
        }
    }
