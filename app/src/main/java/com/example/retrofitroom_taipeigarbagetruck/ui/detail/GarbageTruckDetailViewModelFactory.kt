package com.example.retrofitroom_taipeigarbagetruck.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty

class GarbageTruckDetailViewModelFactory(
    private val garbageTruckProperty: GarbageTruckProperty,
    private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageTruckDetailViewModel::class.java)){
            return GarbageTruckDetailViewModel(garbageTruckProperty, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}