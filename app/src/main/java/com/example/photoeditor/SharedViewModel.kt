package com.example.photoeditor

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val featureName = MutableLiveData<String>()

    private val _image: MutableLiveData<Bitmap> = MutableLiveData()
        val image: LiveData<Bitmap> = _image //track image in real time

        private val _oldimage : MutableLiveData<Bitmap> = MutableLiveData()
        val oldimage : LiveData<Bitmap> = _oldimage

        fun changeImage(newImage: Bitmap){
            _image.postValue(newImage) // update image
        }

        fun saveToOldImage(newImage: Bitmap){
            _oldimage.postValue(newImage)
        }

        fun returnOldImage(){
            changeImage(oldimage.value!!)
        }
}
