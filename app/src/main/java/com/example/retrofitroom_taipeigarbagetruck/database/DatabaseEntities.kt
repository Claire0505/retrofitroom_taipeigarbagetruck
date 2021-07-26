package com.example.retrofitroom_taipeigarbagetruck.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.retrofitroom_taipeigarbagetruck.domain.GarbageTruckProperty

/**
 * Database entities go in this file. These are responsible for reading and writing from the
 * database.
 */
@Entity
data class DatabaseGarbageTruck constructor(
    @PrimaryKey
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
 * Map DatabaseVideos to domain entities
 * 創建一個名為asDomainModel() 的擴展函數。使用該函數將 DatabaseGarbageTruck 數據庫對象轉換為domain域對象。
 */
fun List<DatabaseGarbageTruck>.asDomainModel() : List<GarbageTruckProperty>{
    return  map {
        GarbageTruckProperty(
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