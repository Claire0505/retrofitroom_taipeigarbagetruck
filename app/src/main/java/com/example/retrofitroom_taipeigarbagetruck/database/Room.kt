package com.example.retrofitroom_taipeigarbagetruck.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * 定義兩個幫助程序方法來訪問數據庫。一種輔助方法從數據庫中獲取，另一種方法插入數據庫。
 */

@Dao
interface GarbageTruckDao {
    // 創建一個調用方法getGarbageTruck以從數據庫中獲取所有資料。
    // 將此方法的返回類型更改為LiveData，這樣每當數據庫中的數據發生變化時，UI 中顯示的數據就會刷新。
    @Query("select * from databasegarbagetruck")
    fun getGarbageTruck(): LiveData<DatabaseGarbageTruck>

    // 在界面內，定義另一種insertAll()方法以將從網絡獲取的資料插入到數據庫中。
    // 如果已存在於數據庫中，則覆蓋數據庫條目。為此，請使用onConflict參數將衝突策略設置為REPLACE。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( garbageTrucks: List<DatabaseGarbageTruck>)

}

// 使用@Database註釋將GarbageTrucksDatabase類標記為Room數據庫。
// 聲明DatabaseGarbageTruck屬於該數據庫的實體，並將版本號設置為1。
@Database(entities = [DatabaseGarbageTruck::class] , version = 1)
abstract class GarbageTrucksDatabase: RoomDatabase(){
    abstract val garbageTruckDao: GarbageTruckDao
}

// 創建一個在類外部private lateinit調用的變量INSTANCE，以保存單例對象。
// 該GarbageTrucksDatabase應 獨居，防止發生在同一時間打開數據庫的多個實例。
private lateinit var INSTANCE: GarbageTrucksDatabase

// getDatabase()在類之外創建和定義一個方法。
// 在getDatabase()中，初始化並返回塊INSTANCE內的變量synchronized (同步)。
fun getDatabase(context: Context) : GarbageTrucksDatabase {
    synchronized(GarbageTrucksDatabase::class.java) {
        if (!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            GarbageTrucksDatabase::class.java,
            "garbageTrucks").build()
        }
    }
    return INSTANCE
}