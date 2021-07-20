package com.example.retrofitroom_taipeigarbagetruck.network

data class GarbageTruckProperty(
    val Admin_District: String,
    val Arrival_Time: Int,
    val Bra_num: String,
    val Branch: String,
    val Car_num: String,
    val Departure_Time: Int,
    val Latitude: Double,
    val Location: String,
    val Longitude: Double,
    val Route: String,
    val Train_num: String,
    val Village: String
)
