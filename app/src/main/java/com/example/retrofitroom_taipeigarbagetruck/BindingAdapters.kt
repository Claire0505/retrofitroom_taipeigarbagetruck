package com.example.retrofitroom_taipeigarbagetruck

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty
import com.example.retrofitroom_taipeigarbagetruck.ui.GarbageTruckAdapter

/**
 * 在bindRecyclerView()函數內部，強制轉換recyclerView.adapter為GarbageTruckAdapter，
 * 並adapter.submitList()使用數據調用。這告訴RecyclerView新列表何時可用。
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GarbageTruckProperty>?){
    val adapter = recyclerView.adapter as GarbageTruckAdapter
    adapter.submitList(data)
}
