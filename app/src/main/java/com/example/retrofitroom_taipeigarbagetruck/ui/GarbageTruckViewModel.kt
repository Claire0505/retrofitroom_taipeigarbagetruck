package com.example.retrofitroom_taipeigarbagetruck.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GarbageTruckViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    // 存儲最近響應的內部 MutableLiveData 字符串
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    // 響應字符串的外部不可變 LiveData
    val response: LiveData<String>
    get() = _response

    /**
     * Call getGarbageTruckProperties() on init so we can display status immediately.
     * 在 init 上調用 getGarbageTruckProperties () 以便我們可以立即顯示狀態。
     */
    init {
        getGarbageTruckProperties()
    }

    /**
     * Sets the value of the status LiveData to the Garbage Truck API status.
     */
    private fun getGarbageTruckProperties(){
        _response.value = "Set the Garbage Truck API Response here!"
    }
}