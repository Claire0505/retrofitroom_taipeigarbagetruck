package com.example.retrofitroom_taipeigarbagetruck.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitroom_taipeigarbagetruck.databinding.GarbageTruckItemBinding
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class GarbageTruckAdapter (private val onClicklistener: OnClickListener): ListAdapter<GarbageTruckProperty
        , GarbageTruckAdapter.GarbageTruckViewHolder>(DiffCallback) {

    /**
     *  需要將 GarbageTruckItemBinding 綁定 GarbageTruckProperty 到佈局的變量，
     *  因此將變量傳遞到 GarbageTruckViewHolder. 因為基ViewHolder類在其構造函數中需要一個視圖，
     *  所以將綁定根視圖傳遞給它。 binding.root
     */
    class GarbageTruckViewHolder(private var binding: GarbageTruckItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(garbageTruckProperty: GarbageTruckProperty){
                binding.garbageTruckProperty = garbageTruckProperty

                // 這很重要，因為它強制數據綁定立即執行
                // 這允許 RecyclerView 進行正確的視圖大小測量
                binding.executePendingBindings()
            }

    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     *
     * You have to supply the recycler as the root to the inflate method,
     * so that the inflated view gets the correct layout parameters from the parent
     * 必須將 recycler 作為根 root提供給 inflate 方法，以便膨脹的視圖從父視圖獲取正確的佈局參數
     *
     * You have to set attachToRoot to false, because the recycler view handles the attaching.
     * 必須將 attachToRoot 設置為 false，因為回收站視圖會處理附加操作。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GarbageTruckViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GarbageTruckItemBinding.inflate(layoutInflater, parent, false)

        return GarbageTruckViewHolder(binding)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: GarbageTruckViewHolder, position: Int) {
        val garbageTruckProperty = getItem(position)

        holder.itemView.setOnClickListener {
            onClicklistener.onClick(garbageTruckProperty)
        }
        holder.bind(garbageTruckProperty)
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [GarbageTruckProperty]
     * has been updated.
     */
   companion object DiffCallback : DiffUtil.ItemCallback<GarbageTruckProperty>() {
        // 檢查兩個物件是否是同一個對象，如果是，則不做任何操作，如果不是，則更新這個 Item。
        override fun areItemsTheSame(
            oldItem: GarbageTruckProperty,
            newItem: GarbageTruckProperty
        ): Boolean {
            return oldItem === newItem
        }
        // 檢查成員變數是否一樣來判斷是否要做任何操作，這裡可以依需求自行更換其他成員變數。
        override fun areContentsTheSame(
            oldItem: GarbageTruckProperty,
            newItem: GarbageTruckProperty
        ): Boolean {
            return oldItem.Admin_District == newItem.Admin_District

        }
    }
    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [GarbageTruckProperty]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [GarbageTruckProperty]
     */
    class OnClickListener(val clickListener: (garbageTruckProperty: GarbageTruckProperty) -> Unit){
        fun onClick(garbageTruckProperty: GarbageTruckProperty) = clickListener(garbageTruckProperty)
    }


}

