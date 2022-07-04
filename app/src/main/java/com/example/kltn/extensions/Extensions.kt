package com.example.kltn.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T : Any?> MutableLiveData<T>.toLiveData(): LiveData<T> {
    return this
}