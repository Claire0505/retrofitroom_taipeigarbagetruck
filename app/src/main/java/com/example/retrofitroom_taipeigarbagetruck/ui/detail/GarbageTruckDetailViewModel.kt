package com.example.retrofitroom_taipeigarbagetruck.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.retrofitroom_taipeigarbagetruck.domain.GarbageTruckProperty

class GarbageTruckDetailViewModel(garbageTruckProperty: GarbageTruckProperty,
                                  app: Application) : AndroidViewModel(app) {
    /**
     * 在類定義中，LiveData為選定的 GarbageTruckProperty 屬性添加，以將該信息公開給詳細信息視圖。
     * 按照通常的模式創建一個MutableLiveData來保存它GarbageTruckProperty本身，
     * 然後公開一個不可變的公共LiveData屬性。
     */
    private val _selectedProperty = MutableLiveData<GarbageTruckProperty>()
    val selectedProperty: LiveData<GarbageTruckProperty>
    get() = _selectedProperty

    // 創建一個init {}塊並MarsProperty使用構造函數中的對象設置所選 Mars 屬性的值。
    init {
        _selectedProperty.value = garbageTruckProperty
    }

}