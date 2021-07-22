# UsingRetrofitRoom TaipeiGarbageTruck

## 第 1 步：在 build.gradle (app)中添加導航片段和數據綁定相關依賴庫
1. 啟用DataBinding (要使用數據綁定，需要使用< layout >標籤包裝XML佈局)
```kotlin
// 啟用DataBinding (要使用數據綁定，需要使用<layout>標籤包裝XML佈局)
    buildFeatures {
        dataBinding true
    }
```
2. 在dependencies{ } 添加 Navigation
```kotlin
// navigation
def nav_version = "2.3.5"

implementation
("androidx.navigation:navigation-fragment-ktx:$nav_version")

implementation
("androidx.navigation:navigation-ui-ktx:$nav_version")   
```
3. Navigation 使用 Safe Args 傳遞類型安全的數據
```kotlin
plugins {
    //使用到Kotlin kapt項目中如Dagger或Data Binding 之類的庫。
    id 'kotlin-kapt'

    // Safe Args插件都會生成一個相應的NavDirection類。
    // 這些類表示所有應用程序操作的導航。
    id("androidx.navigation.safeargs")
}

再到 build.gradle 中的 在dependencies{ }裡面加上
// navigation 使用 Safe Args 傳遞類型安全的數據
def nav_version = "2.3.5"
 classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")

```
4. 將導航圖添加到項目，建立 res/navigation/navigation.xml
5. 修改 activity_main.xml
```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".ui.MainActivity">

        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/nav_host_fragment_container"
            android:name="androidx.navigation.fragment.NavHostFragment"
            app:navGraph="@navigation/navigation"
            app:defaultNavHost="true"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```
---
## 第二步：Databinding 數據綁定到 ViewModel
1. 打開 ui/GarbageTurckFragemnt.kt 初始化 GarbageTruckViewModel，這意味著 GarbageTruckViewModel 被創建使用它的第一次

```kotlin
 /**
  *  Lazily initialize our [GarbageTruckViewModel]
  */
    private val viewModel : GarbageTruckViewModel by lazy {
        ViewModelProvider(this).get(GarbageTruckViewModel::class.java)
    }
```
2. 檢查 onCreateView()方法，此方法 fragment_garbage_truck.xml  
使用數據邦定對佈局進行膨脹，將綁定生命週期所有者，任何 LiveData 在數據綁定使用都將自動觀察到任何更改，並且將 UI 相應地更新。

```kotlin
 override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = 
        FragmentGarbageTruckBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        // 允許數據綁定使用此 Fragment 的生命週期觀察 LiveData
        binding.lifecycleOwner = this

        // Giving the binding access to the GarbageTruckViewModel
        // 允許數據綁定使用此 Fragment 的生命週期觀察 LiveData
        binding.viewModel = viewModel

        return binding.root

    }
```
打開fragment_garbage_truck.xml。這是目前使用的概覽片段的佈局，它包括視圖模型的數據綁定。它導入OverviewViewModel，然後將響應response 從綁定ViewModel到 TextView。在以後的代碼中，將文本視圖替換RecyclerView。
```xml
 <data>
    <variable name="viewModel"
    type="com.example.retrofitroom_taipeigarbagetruck.ui. GarbageTruckViewModel" />

</data>

<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@{viewModel.response}" />
```
3. 打開 ui/GarbageTruckViewModel， Because the response is a 
LiveData and we've set the lifecycle for the binding variable,
any change to it will update the app UI.

4. 檢查 init 塊，當 ViewModel 被創建時，它會調用該getGarbageTruckProperties() 方法
5. 檢查 getGarbageTruckProperties() 方法，在目前此應用程序中，此方法包含佔位符響應。對於本程式碼目標是更新響應LiveData的範圍內ViewModel，到時將從互聯網上得到使用真實數據。

```kotlin
class GarbageTruckViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    // 存儲最近響應的內部 MutableLiveData 字符串
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    // 響應字符串的外部不可變 LiveData
    val response: LiveData<String>
    get() = _response

    /**
     * Call getGarbageTruckProperties() on init so we can display status immediately.
     * 在 init 上調用 getGarbageTruckProperties () 以便我們可以立即顯示狀態。
     */
    init {
        getGarbageTruckProperties()
    }

    /**
     * Sets the value of the status LiveData to the Garbage Truck API status.
     */
    private fun getGarbageTruckProperties(){
        _response.value = "Set the Garbage Truck API Response here!"
    }
}
```
6. 編譯並運行應用程序。在此應用程序的當前版本中，看到的畫面顯示只是啟動器響應 "Set the Garbage Truck API Response here!"
---
# 使用 Retrofit 連接到 Web 服務
來自 Web 服務的響應通常使用JSON 進行格式化， JSON是一種用於表示結構化數據的交換格式。簡短的解釋是 JSON 對像是鍵值 key-value 的集合，有時稱為字典 (dirctionary)、哈希映射(hash map)或關聯數組(associative array)。JSON 對象的集合是一個 JSON 數組。該數組是您從 Web 服務返回的響應。

要將此 JSON 數據導入應用程序，應用程序需要與服務器建立網絡連接，與該服務器通信，然後接收 JSON 響應數據並將其解析為應用程序可以使用的格式。

## 第 1 步：向 Gradle 添加 Retrofit 依賴項
```kotlin
// 應用程序需要與服務器建立網絡連接，將使用名為 Retrofit 庫來建立連接。
    def retrofit_version = "2.9.0"
    implementation
    ("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation 
    ("com.squareup.retrofit2:converter-scalars:$retrofit_version")
 ```  
## 第 2 步：實施 GarbageTruckApiService

Retrofit 根據來自 Web 服務的內容為應用程序創建網絡 API。它從 Web 服務獲取數據並通過一個單獨的轉換器庫路由它，該轉換器庫知道如何解碼數據並以有用對象的形式返回它。Retrofit 包括對流行的 Web 數據格式（如 XML 和 JSON）的內置支持。Retrofit 最終會為您創建大部​​分網絡層，包括關鍵細節，例如在後台線程上運行請求。

1. 創建 app/java/network/GarbageTruckApiService.kt 
現在該文件只包含一件事：Web 服務的基本 URL 的常量。
```kotlin
private const val BASE_URL = "https://www.dropbox.com/s/o75vb07ujb3r1xb/Taipei_GarbageTruck_2021.json?dl=1"
```
2. 在該常量下方，使用 Retrofit 構建器創建一個 Retrofit 對象。導入retrofit2.Retrofit 並在需要時 
retrofit2.converter.scalarsScalarsConverterFactory

```kotlin
/**
 * https://www.dropbox.com/s/o75vb07ujb3r1xb/Taipei_GarbageTruck_2021.json?dl=1
 *
 * 使用 Retrofit2 進行 API 解析時出現錯誤
 * java.lang.IllegalArgumentException: baseUrl 必須以 / 結尾
 * 需將網址分成兩部分  BASE_URL = "https://www.dropbox.com/ 和 KEY = "Taipei_GarbageTruck_2021.json?dl=1"
 * 然後像這樣在 GET 註釋中添加  @GET("s/o75vb07ujb3r1xb/"+ KEY)
 */
private const val BASE_URL = "https://www.dropbox.com/"
private const val KEY = "Taipei_GarbageTruck_2021.json?dl=1"
 ```   
Retrofit 至少需要兩個可用的東西來構建 Web 服務 API：Web 服務的基本 URI 和轉換器工廠。轉換器告訴 Retrofit 如何處理它從 Web 服務返回的數據。在這種情況下，您希望 Retrofit 從 Web 服務獲取 JSON 響應，並將其作為String. Retrofit 具有ScalarsConverter支持字符串和其他原始類型的 ，因此您可以addConverterFactory()使用ScalarsConverterFactory. 最後，您調用build()以創建 Retrofit 對象。

3. 在調用 Retrofit 構建器的正下方，定義一個接口，該接口定義 Retrofit 如何使用 HTTP 請求與 Web 服務器通信。導入retrofit2.http.GET並在需要時 retrofit2.Call。

```kotlin
interface GarbageTruckApiService {
    @GET("s/o75vb07ujb3r1xb/"+ KEY)
    fun getProperties():
            Call<String>
}
```
現在的目標是從 Web 服務獲取 JSON 響應字符串，您只需要一種方法即可：getProperties(). 要告訴 Retrofit 此方法應該做什麼，請使用@GET註釋並指定該 Web 服務方法的路徑或端點。
在這種情況下，端點稱為 (s/o75vb07ujb3r1xb/"+ KEY)。

調用該getProperties()方法時，Retrofit 將端點附加 
(s/o75vb07ujb3r1xb/"+ KEY)到基本 URL（您在 Retrofit 構建器中定義），並創建一個Call對象。該對Call像用於啟動請求。

4. 在GarbageTruckApiService接口下方，定義一個公共對象，
調用它GarbageTruckApi來初始化 Retrofit 服務。

```kotlin
object  GarbageTruckApi {
    val retrofitService : GarbageTruckApiService by lazy {
        retrofit.create(GarbageTruckApiService::class.java)
    }
}
```
Retrofit.create()方法使用 GarbageTruckApi interface創建 Retrofit 服務本身。由於此調用的計算成本很高，因此您可以lazy初始化 Retrofit 服務。並且由於應用程序只需要一個 Retrofit 服務實例，您可以使用一個名為 的公共對象將該服務公開給應用程序的其餘部分。現在一旦所有設置完成，每次您的應用程序調用 GarbageTruckApi.retrofitService，它都會獲得一個實現GarbageTruckApiService.

## 第三步：在 GarbageTruckViewModel中調用web服務
1. 打開 GarbageTruckViewModel.kt。向下滾動到getGarbageTruckProperties()方法
2. 刪除_response.value = "Set the Mars API Response here!"。

3. 在裡面getGarbageTruckProperties()，添加如下所示的代碼。
import retrofit2.Callback並在需要時
import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckApi。

該GarbageTruckApi.retrofitService.getProperties()方法返回一個Call對象。然後您可以調用該enqueue()對像以在後台線程上啟動網絡請求。

```kotlin
private fun getGarbageTruckProperties(){
        GarbageTruckApi.retrofitService.getProperties().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                   _response.value = response.body()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _response.value = "Failure: " + t.message
                }

            }
        )
    }
 ```  
 ## 第四步：定義 Internet 權限
  打開app/manifests/AndroidManifest.xml。
  在 <application標籤>之前添加這一行：
  ```kotlin
  <uses-permission android:name="android.permission.INTERNET" />
  ```
  再次編譯並運行應用程序。如果您的互聯網連接一切正常，您會看到包含 GarbageTruck Property 數據的 JSON 文本。
  
  ---
  # 使用 Moshi 解析 JSON 響應

  現在您從服務獲得 JSON 響應，但有一個名為Moshi的庫 ，它是一個 Android JSON 解析器，可將 JSON 字符串轉換為 Kotlin 對象。Retrofit 有一個可與 Moshi 配合使用的轉換器，因此它是一個非常適合您的庫。
  
## 第一步：添加Moshi庫依賴
1. 打開build.gradle (Module: app)。
```kotlin
 // moshi for parsing the JSON format
    def moshi_version = "1.12.0"
    implementation
     ("com.squareup.moshi:moshi-kotlin:$moshi_version")
````    
2. 在dependencies塊中找到 Retrofit 標量轉換器的行：
implementation "com.squareup.retrofit2:converter-scalars:$version_retrofit"

3. 更改這些行以使用converter-moshi：
```kotlin
 implementation 
 ("com.squareup.retrofit2:converter-moshi:$retrofit_version")
 ```
 4. 單擊立即同步以使用新的依賴項重建項目。

 ## 實現 GarbageTruckProperty 數據類
 從 Web 服務獲得的 JSON 響應的示例條目如下所示：
 https://www.dropbox.com/s/o75vb07ujb3r1xb/Taipei_GarbageTruck_2021.json?dl=1

 ```json
 [
  {
    "Admin_District": "中山區",
    "Village": "力行里",
    "Branch": "長安分隊",
    "Bra_num": "100-021",
    "Car_num": "119-BQ",
    "Route": "長安-3",
    "Train_num": "第1車",
    "Arrival_Time": 1630,
    "Departure_Time": 1638,
    "Location": "臺北市中山區建國北路一段69號前",
    "Longitude": 121.5369444,
    "Latitude": 25.05111111
  },{...}]
  
  ```
上面顯示的 JSON 響應是一個數組，由 [ ] 方括號表示。
該數組包含用 { } 花括號括起來的 JSON 對象。每個對像都包含一組名稱-值對，以 : 冒號分隔。名稱用 " " 引號括起來。值可以是數字、字符串和布爾值，也可以是其他對像或數組。如果一個值是一個字符串，它也被引號包圍。

Moshi 解析此 JSON 數據並將其轉換為 Kotlin 對象。為此，它需要有一個 Kotlin 數據類來存儲解析結果，因此下一步是創建該類。
1. 點選 app/java/network/ -> 按右鍵選 new ->
 kotlin data class File from Json 功具轉換，將要解析的 Json 資料，
 全部貼到上面去，它會幫你自動轉成定義的：

 ```kotlin
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
```
## 第三步：更新GarbageTruckApiService 和 GarbageTruckViewModel

隨著GarbageTruckProperty就位數據類，現在可以更新網絡API，並ViewModel以包括Moshi data。
1. 打開network/GarbageTruckApiService.kt。會看到ScalarsConverterFactory. 這是因為您在步驟 1 中所做的 Retrofit 依賴項更改。您很快就會修復這些錯誤。

2. 在文件頂部，就在 Retrofit 構建器之前，添加以下代碼以創建 Moshi 實例。導入com.squareup.moshi.Moshi並在需要時 import 
com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory。

```kotlin
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
```
與您使用 Retrofit 所做的類似，這裡您moshi使用 Moshi 構建器創建一個對象。為了讓 Moshi 的註解與 Kotlin 一起正常工作，添加KotlinJsonAdapterFactory，然後調用build()。

3. 將 Retrofit 構建器更改為使用MoshiConverterFactory代替ScalarConverterFactory，並傳入moshi您剛剛創建的實例。retrofit2.converter.moshi.MoshiConverterFactory請求時導入。

```kotlin
private val retrofit = Retrofit.Builder()
   .addConverterFactory(MoshiConverterFactory.create(moshi))
   .baseUrl(BASE_URL)
   .build()
 ```
4. 也刪除import retrofit2.converter.scalars.ScalarsConverterFactory

5. 更新GarbageTruckApiService 接口以讓 Retrofit 返回一個MarsProperty對象列表，而不是返回Call< String >.
```kotlin
interface GarbageTruckApiService {
    @GET("s/o75vb07ujb3r1xb/"+ KEY)
    fun getProperties():
            Call<List<GarbageTruckProperty>>
}
```
6. 打開GarbageTruckViewModel.kt。向下滾動到調用getProperties().enqueue()的getMarsRealEstateProperties()方法。

7. 將參數更改為enqueue()fromCallback< String >
到Callback< List< GarbageTruckProperty>> 。
請求時 import com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty
```kotlin
GarbageTruckApi.retrofitService.getProperties().enqueue(
           object: Callback<List<GarbageTruckProperty>> {
```
8. 在中onFailure() 和 onResponse()中，將參數從更改Call< String >
為 Call< List< GarbageTruckProperty > >

9. 在onResponse()，將現有的分配替換為_response.value如下所示的分配。因為response.body()現在是一個GarbageTruckProperty對象列表，該列表的大小是被解析的屬性數。此響應消息將打印該數量的屬性：

```kotlin
 GarbageTruckApi.retrofitService.getProperties().enqueue(
           object: Callback<List<GarbageTruckProperty>> {
               override fun onResponse(
                   call: Call<List<GarbageTruckProperty>>,
                   response: Response<List<GarbageTruckProperty>>
               ) {
                   _response.value = "Success ${response.body()?.size} GarbageTruck properties retrieved"
               }

               override fun onFailure(call: Call<List<GarbageTruckProperty>>, t: Throwable) {
                   _response.value = "Failure: " + t.message
               }
           }
        )
```
---

# 在 Retrofit 中使用協程 (Use coroutines with Retrofit)

現在 Retrofit API 服務正在運行，但它使用Call< List< GarbageTruckProperty > >一個回調和兩個您必須實現的回調方法。
一種方法處理成功，另一種方法處理失敗，失敗結果報告異常。

如果您可以使用具有異常處理的協程而不是使用回調，您的代碼將更高效且更易於閱讀。在此任務中，您將轉換網絡服務並ViewModel使用協程。

## 第一步：更新GarbageTruckApiService和 GarbageTruckViewModel
1. 在 中GarbageTruckApiService，做getProperties()一個掛起函數。
更改Call< List< GarbageTruckProperty > >為 List< MarsProperty >。該getProperties()方法如下所示：
```kotlin
interface GarbageTruckApiService {
    @GET("s/o75vb07ujb3r1xb/" + KEY)
    suspend fun getProperties(): List<GarbageTruckProperty>
}
```
2. 在GarbageTruckViewModel.kt文件中，刪除getMarsRealEstateProperties()裡面的所有代碼。
您將在此處使用協程，而不是調用enqueue()和onFailure()和onResponse()回調。
3. 在getMarsRealEstateProperties()裡面，使用啟動協程viewModelScope. ViewModelScope是為ViewModel應用程序中的每個定義的內置協程範圍。如果ViewModel清除了，則在此範圍內啟動的任何協程都會自動取消。
4. 在啟動塊內，添加一個try/catch塊來處理異常
5. 裡面try {} 塊，叫getProperties()上retrofitService對象：調用getProperties()從MarsApi服務創建並啟動在後台線程網絡通話。
6. 同樣在try {}塊內，更新成功響應的響應消息
7. 在catch {}塊內，處理失敗響應

完整的getMarsRealEstateProperties()方法現在看起來像這樣：
```kotlin
private fun getGarbageTruckProperties(){
        viewModelScope.launch {
            try {
                val listResult = GarbageTruckApi.retrofitService.getProperties()
                _response.value = "Success: ${listResult.size} GarbageTruck properties retrieved"
            } catch (e: Exception){
                _response.value = "Failure: ${e.message}"
            }
        }
    }
```
8. 編譯並運行應用程序。這次您得到與上一個任務相同的結果（屬性數量的報告），但代碼和錯誤處理更直接。
--- 
# 從互聯連獲取數據總結
 REST 網絡服務
* 一個Web服務是在使您的應用程序發出請求和找回數據互聯網提供基於軟件的功能。

* 常見的 Web 服務使用 REST架構。提供 REST 架構的 Web 服務稱為RESTful服務。RESTful Web 服務是使用標準 Web 組件和協議構建的。

* 您可以通過 URI 以標準化方式向 REST Web 服務發出請求。
* 要使用 Web 服務，應用程序必須建立網絡連接並與服務通信。然後應用程序必須接收響應數據並將其解析為應用程序可以使用的格式。

* 該 Retorift 庫是一個客戶端庫，使您的應用程序，使請求到REST Web服務。
* 使用轉換器告訴 Retrofit 如何處理它發送到 Web 服務並從 Web 服務返回的數據。例如，ScalarsConverter轉換器將 Web 服務數據視為一個String或其他原語。
* 要使您的應用能夠連接到互聯網，請"android.permission.INTERNET"在 Android 清單中添加權限。
---
JSON 解析
* 來自 Web 服務的響應通常採用 JSON格式，這是一種用於表示結構化數據的常見交換格式。
* JSON 對像是鍵值對的集合。該集合有時稱為dictionary、hash map或關聯數組。
* JSON 對象的集合是一個 JSON 數組。您從 Web 服務獲得一個 JSON 數組作為響應。
* 鍵值對中的鍵用引號括起來。值可以是數字或字符串。字符串也被引號包圍。
* 該 moshi 是一個Android JSON解析器，轉換一個JSON字符串對象kotlin。Retrofit 有一個可與 Moshi 配合使用的轉換器。
* Moshi 將 JSON 響應中的鍵與數據對像中具有相同名稱的屬性進行匹配。
* 要為鍵使用不同的屬性名稱，請使用@Json註釋和 JSON 鍵名稱對該屬性進行註釋。

>參考教學：[Android Kotlin 基礎：8.1 從互聯網獲取數據](https://developer.android.com/codelabs/kotlin-android-training-internet-data?index=..%2F..android-kotlin-fundamentals#0)
---
# 改從 viewModel顯示單一清運資料 (測試)
## 第 1 步：更新視圖模型
1. 打開 GarbageTruckViewModel.kt。在LiveDatafor下方，
為_response單個GarbageTruckProperty對象添加內部（可變）和外部（不可變）實時數據。
```kotlin
 // 為_response單個對象添加內部（可變）和外部（不可變）實時數據。
    private val _property = 
    MutableLiveData<GarbageTruckProperty>()

    val property: LiveData<GarbageTruckProperty>
    get() = _property
 ```
 2. 在getMarsRealEstateProperties()方法中，找到try/catch {}設置_response.value為屬性數量的塊內的行。
 在try/catch. 如果GarbageTruckProperty對象可用，此測試將值設置_property LiveData為 中的第一個屬性listResult。
 ```kotlin
    if (listResult.isNotEmpty()){
        _property.value = listResult[0]
    } 
 ```
 3. 打開res/layout/fragment_garbage_truck.xml。在< TextView>元素中，更改android:text綁定
 ```kotlin
 android:text="@{viewModel.property.location}"  
 ```
 4. 運行應用程序。該TextView會顯示第一個地址。  
 ---
 # 使用 RecyclerView 顯示清運資料
 ## 第 1 步：更新視圖模型
 1. 打開GarbageTruckViewModel.kt。
 2. 將_property改為列表對象
 ```kotlin
    private val _property = MutableLiveData<List<GarbageTruckProperty>>()

    val property: LiveData<List<GarbageTruckProperty>>
    get() = _property
 ```
 3. 向下滾動到getMarsRealEstateProperties()方法。在try {}塊內，用下面顯示的行替換您在上一個任務中添加的整個測試。

 ```kotlin
  viewModelScope.launch {
     try {
        _property.value = GarbageTruckApi.retrofitService.getProperties()

     } catch (e: Exception){
        _response.value = "Failure: ${e.message}"
     }
}
```
## 第 2 步：更新佈局和片段
下一步是更改應用程序的佈局和片段以使用 recyclerView和 LinearLayoutManager，而不是單個圖像視圖。

1. 創建res/layout/garbage_truck_item.xml。
將數據綁定並將變量重命名為"garbageTruckProperty"。

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="garbageTruckProperty"
            type="com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp">

        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:layout_marginEnd="16dp"
            android:layout_marginBottom="8dp"
            android:layout_marginTop="8dp"
            app:cardCornerRadius="10dp"
            app:cardElevation="5dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:layout_marginEnd="16dp"
                android:orientation="vertical">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <TextView
                        android:id="@+id/tv_Admin_District"
                        android:padding="2dp"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.admin_District}"
                        tools:text="中山區" />

                    <TextView
                        android:id="@+id/tv_Village"
                        android:padding="2dp"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.village}"
                        tools:text="力行里" />

                    <TextView
                        android:id="@+id/tv_Branch"
                        android:padding="2dp"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.branch}"
                        tools:text="長安分隊" />

                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="3dp"
                    android:orientation="horizontal"
                    android:visibility="invisible">

                    <TextView
                        android:id="@+id/tv_latitude"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.latitude}"
                        tools:text="25.05111111" />

                    <TextView
                        android:id="@+id/tv_longitude"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.longitude}"
                        tools:text="121.5369444" />

                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:padding="2dp"
                    android:orientation="horizontal">

                    <TextView
                        android:id="@+id/textView3"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/arrival_time"
                        android:textColor="@color/gray" />

                    <TextView
                        android:id="@+id/tv_Arrival_Time"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.arrival_Time}"
                        android:textColor="@color/green"
                        android:textSize="16sp"
                        tools:text="1630" />

                    <TextView
                        android:id="@+id/textView5"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/departure_time"
                        android:textColor="@color/gray" />

                    <TextView
                        android:id="@+id/tv_Departure_Time"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.departure_Time}"
                        android:textColor="@color/green"
                        android:textSize="16sp"
                        tools:text="1638" />
                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:padding="4dp">

                    <TextView
                        android:id="@+id/tv_Location"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="@{garbageTruckProperty.location}"
                        android:textColor="@color/gray"
                        tools:text="臺北市中山區建國北路一段69號前" />
                </LinearLayout>

            </LinearLayout>
        </androidx.cardview.widget.CardView>
    </FrameLayout>
</layout>
```
2. 打開res/layout/fragment_garbage_truck.xml。<br/>刪除整個< TextView>元素。
3. 改為<RecyclerView >添加此元素，它使用 LinearLayoutManager和garbage_truck_item單個項目的佈局：.
```xml
 <androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="4dp"
    tools:context=".ui.GarbageTruckFragment">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layoutManager=
        "androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:listData="@{viewModel.property}"
        tools:listitem="@layout/garbage_truck_item" />

</androidx.constraintlayout.widget.ConstraintLayout>
``` 
## 第 3 步：添加GarbageTruckAdapter適配器                

1. 創建GarbageTruckAdapter.kt，構造函數參數如下所示。<br/>
在GarbageTruckAdapter類擴展ListAdapter，它的構造需要列表類型<br/>
觀點持有者和DiffUtil.ItemCallback實施。

需要時導入androidx.recyclerview.widget.ListAdapter
>ListAdapter
ListAdapter 是谷歌在 2018 新增的一個用來優化 RecyclerView.Adapter 的類，能夠用更簡潔的代碼完成 Adapter，並且加入了 DiffUtil 這個輔助類，優化了 RecyclerView 的效能。
```kotlin
class GarbageTruckAdapter : ListAdapter<GarbageTruckProperty
        , GarbageTruckAdapter.GarbageTruckViewHolder>(DiffCallback) {
```
DiffCallback 為自定義的一個 compaion object DiffCallback

2. 單擊GarbageTruckAdapter類中的任意位置按Control+i
以實現ListAdapter方法，<br/>
即onCreateViewHolder()和onBindViewHolder()。

```kotlin
/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class GarbageTruckAdapter : ListAdapter<GarbageTruckProperty
        , GarbageTruckAdapter.GarbageTruckViewHolder>(DiffCallback) {

     /**
     *  需要將 GarbageTruckItemBinding 綁定  GarbageTruckProperty 到佈局的變量，
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
        holder.bind(garbageTruckProperty)
    }
 ```   

3. 在 GarbageTruckAdapter 類定義的末尾，在您剛剛添加的方法之後，為添加一個伴隨對象定義DiffCallback，如下所示。

androidx.recyclerview.widget.DiffUtil請求時導入。<br/>
該DiffCallback對象擴展DiffUtil.ItemCallback與compare-對象的類型GarbageTruckProperty。
```kotlin
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
 ```   
 ## 第 4 步：Add the binding adapter and connect the parts
 最後，使用列表對象 BindingAdapter初始化。設置數據會導致數據綁定自動觀察對象列表。然後當列表改變時自動調用綁定適配

 1. 建立 BindingAdapters.kt <br/>
 綁定適配器是位於視圖和綁定數據之間的擴展方法，用於在數據更改時提供自定義行為。
 2. 在文件的末尾，添加一個bindRecyclerView()<br/>
 將RecyclerView 和 GarbageTruckProperty 對象列表作為參數的方法。使用@BindingAdapter.

 ```kotlin
/**
 * 在bindRecyclerView()函數內部，強制轉換recyclerView.adapter為GarbageTruckAdapter，
 * 並adapter.submitList()使用數據調用。這告訴RecyclerView新列表何時可用。
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GarbageTruckProperty>?){
    val adapter = recyclerView.adapter as GarbageTruckAdapter
    adapter.submitList(data)
}
```
3. 打開res/layout/fragment_garbage_truck.xml。<br/>
將 app:listData 屬性添加到RecyclerView元素 <br/>
並將其設置為viewmodel.properties使用數據綁定。

```kotlin
 app:listData="@{viewModel.property}"
```
4. 打開 GarbageTruckFragment.kt。<br/>
在onCreateView()，就在調用 之前setHasOptionsMenu()，將RecyclerView適配器初始化binding.recycler為一個新GarbageTruckAdapter對象。

``` kotlin
// Sets the adapter of the  recycler RecyclerView
 binding.recycler.adapter = GarbageTruckAdapter()
 ```
 5. 運行應用程序。會顯示到GarbageTruckProperty的 RecyclerView。
 ---
 