package com.psyma17.healthyweightapplication.ui.weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeightFragmentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is weight Fragment"
    }
    val text: LiveData<String> = _text
}