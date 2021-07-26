package com.example.retrofitroom_taipeigarbagetruck.network

import com.example.retrofitroom_taipeigarbagetruck.database.DatabaseGarbageTruck
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *  DataTransferObjects 在這個文件中。這些負責解析來自服務器的響應 * 或格式化對像以發送到服務器。
 *
 * @see domain package for
 */
@JsonClass(generateAdapter = true)
data class NetworkGarbageTruckContainer (val garbageTrucks : List<NetworkGarbageTruck>)

@JsonClass(generateAdapter = true)
data class NetworkGarbageTruck(
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
)

/**
 * Convert Network results to database objects
 * 將網絡結果轉換為數據庫對象
 * 創建一個名為asDatabaseModel(). 使用該函數將網絡對象轉換為 DatabaseGarbageTruck 數據庫對象。
 */
fun NetworkGarbageTruckContainer.asDatabaseModel(): List<DatabaseGarbageTruck> {
    return garbageTrucks.map {
        DatabaseGarbageTruck(
            Admin_District = it.Admin_District,
            Bra_num = it.Bra_num,
            Branch = it.Branch,
            Car_num = it.Car_num,
            Arrival_Time = it.Arrival_Time,
            Departure_Time = it.Departure_Time,
            Location = it.Location,
            Latitude = it.Latitude,
            Longitude = it.Longitude,
            Route = it.Route,
            Train_num = it.Train_num,
            Village = it.Village
        )
    }
}