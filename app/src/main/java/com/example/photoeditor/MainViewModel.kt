package com.example.photoeditor

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData

class MainViewModel : ViewModel() {

    private val _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> = _image //track image in real time

    fun changeImage(newImage: Bitmap){
        _image.postValue(newImage) // update image
    }

}