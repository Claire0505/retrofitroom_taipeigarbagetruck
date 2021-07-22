package com.example.retrofitroom_taipeigarbagetruck.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckApi
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GarbageTruckViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    // 存儲最近響應的內部 MutableLiveData 字符串
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    // 響應字符串的外部不可變 LiveData
    val response: LiveData<String>
    get() = _response

    // 為_response單個對象添加內部（可變）和外部（不可變）實時數據。
    private val _property = MutableLiveData<List<GarbageTruckProperty>>()
    val property: LiveData<List<GarbageTruckProperty>>
    get() = _property

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
        viewModelScope.launch {
            try {
                _property.value = GarbageTruckApi.retrofitService.getProperties()
                _response.value = "Success: Mars properties retrieved"

            } catch (e: Exception){
                _response.value = "Failure: ${e.message}"
            }
        }
    }
}