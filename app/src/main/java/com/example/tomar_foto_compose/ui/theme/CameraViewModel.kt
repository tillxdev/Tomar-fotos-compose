package com.example.tomar_foto_compose

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private val _photos = mutableStateListOf<Bitmap>()
    val photos: List<Bitmap> get() = _photos

    fun addPhoto(photo: Bitmap) {
        _photos.add(photo)
    }
}

