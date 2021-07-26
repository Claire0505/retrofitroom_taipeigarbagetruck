package com.example.retrofitroom_taipeigarbagetruck.network

import com.example.retrofitroom_taipeigarbagetruck.domain.GarbageTruckProperty
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * https://www.dropbox.com/s/o75vb07ujb3r1xb/Taipei_GarbageTruck.json?dl=1
 * 使用 Retrofit2 進行 API 解析時出現錯誤
 * java.lang.IllegalArgumentException: baseUrl 必須以 / 結尾
 * 需將網址分成兩部分  BASE_URL = "https://www.dropbox.com/ 和 KEY = "Taipei_GarbageTruck_2021.json?dl=1"
 * 然後像這樣在 GET 註釋中添加  @GET("s/o75vb07ujb3r1xb/"+ KEY)
 */
private const val BASE_URL = "https://www.dropbox.com/"
private const val KEY = "Taipei_GarbageTruck.json?dl=1"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface GarbageTruckApiService {
    @GET("s/o75vb07ujb3r1xb/" + KEY)
    suspend fun getProperties(): List<GarbageTruckProperty>
}

object GarbageTruckApi {
    val retrofitService: GarbageTruckApiService by lazy {
        retrofit.create(GarbageTruckApiService::class.java)
    }
}