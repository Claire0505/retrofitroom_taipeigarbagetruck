package com.example.retrofitroom_taipeigarbagetruck

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitroom_taipeigarbagetruck.domain.GarbageTruckProperty
import com.example.retrofitroom_taipeigarbagetruck.ui.GarbageTruckAdapter
import com.example.retrofitroom_taipeigarbagetruck.viewmodels.GarbageTruckApiStatus

/**
 * 在bindRecyclerView()函數內部，強制轉換recyclerView.adapter為GarbageTruckAdapter，
 * 並adapter.submitList()使用數據調用。這告訴RecyclerView新列表何時可用。
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GarbageTruckProperty>?){
    val adapter = recyclerView.adapter as GarbageTruckAdapter
    adapter.submitList(data)
}

@BindingAdapter("garbageTruckApiStatus")
fun bindStatus(statusImageView: ImageView, status: GarbageTruckApiStatus?){
    when (status) {
        GarbageTruckApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        GarbageTruckApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        GarbageTruckApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
