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

// 在視圖模型中創建一個來表示 Web 請求的狀態。需要考慮三種狀態——加載、成功和失敗。
enum class GarbageTruckApiStatus { LOADING, ERROR, DONE}

class GarbageTruckViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<GarbageTruckApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<GarbageTruckApiStatus>
    get() = _status

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

    // 當用戶點擊recyclerview時，它應該觸發導航到一個片段，該片段顯示有關單擊項目的詳細信息。
    // LiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<GarbageTruckProperty>()
    val navigateToSelectedProperty: LiveData<GarbageTruckProperty>
        get() = _navigateToSelectedProperty

    /**
     * Sets the value of the status LiveData to the Garbage Truck API status.
     */
    private fun getGarbageTruckProperties(){
        viewModelScope.launch{
            _status.value = GarbageTruckApiStatus.LOADING
            try {
                _property.value = GarbageTruckApi.retrofitService.getProperties()
                _status.value = GarbageTruckApiStatus.DONE

            } catch (e: Exception) {
                _status.value = GarbageTruckApiStatus.ERROR
                // 將設置_property LiveData為空列表。這將清除RecyclerView.
                _property.value = ArrayList()
            }
        }
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [GarbageTruckProperty] that was clicked on.
     */
    fun displayPropertyDetails(garbageTruckProperty: GarbageTruckProperty){
        _navigateToSelectedProperty.value = garbageTruckProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

}