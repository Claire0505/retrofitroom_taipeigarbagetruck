package com.example.retrofitroom_taipeigarbagetruck.network


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 *  @Parcelize 序列化註解實際上就是幫我們自動生成了 writeToParcel() 和 createFromParcel()，
 *  這一點可以節約我們的代碼量。需要在build.gradle加上插件  id 'kotlin-parcelize'
 *  import kotlinx.parcelize.Parcelize
 */
@Parcelize
data class GarbageTruckProperty(
    val Admin_District: String,
    val Bra_num: String,
    val Branch: String,
    val Car_num: String,
    val Arrival_Time: String,
    val Departure_Time: String,
    val Location: String,
    val Latitude: String,
    val Longitude: String,
    val Route: String,
    val Train_num: String,
    val Village: String
) : Parcelable
