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
 # 在RecyclerView中添加錯誤處理
 在此任務中，將添加基本的錯誤處理，以使用戶更好地了解發生了什麼。<br/>
 如果互聯網不可用，應用程序將顯示連接錯誤圖標。當應用程序正在獲取GarbageTruckProperty列表時，應用程序將顯示加載動畫。

## 第 1 步：向視圖模型(viewModel)添加狀態
 1. 打開 GarbageTruckViewModel.kt。在文件頂部（導入之後，類定義之前），添加一個enum代表所有可用狀態：
 ```kotlin
 enum class MarsApiStatus { LOADING, ERROR, DONE }
 ```
 2. 將_response 整個 GarbageTruckViewModel類中的<br/>
 內部和外部實時數據定義重命名為_status.
 ```kotlin
  // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<GarbageTruckApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<GarbageTruckApiStatus>
    get() = _status
 ```
 3. 向下滾動到getGarbageTruckProperties()方法並更新_response到_status這裡。將"Success"字符串更改為 GarbageTruckApiStatus.DONE狀態，<br/>將"Failure"字符串更改為 GarbageTruckApiStatus.ERROR。  

 4. 將狀態設置 GarbageTruckApiStatus.LOADING為try{}塊之前。<br/>
 這是協程運行並且您正在等待數據時的初始狀態。

 5. 在catch {}塊中的錯誤狀態之後，將 設置_property LiveData為空列表。這將清除RecyclerView.

 ```kotlin
 private fun getGarbageTruckProperties(){
        viewModelScope.launch{
            _status.value = GarbageTruckApiStatus.LOADING
            try {
                _property.value = GarbageTruckApi.retrofitService.getProperties()
                _status.value = GarbageTruckApiStatus.DONE

            } catch (e: Exception) {
                _status.value = GarbageTruckApiStatus.ERROR
                // 將設置_property LiveData為空列表。這將清除RecyclerView.
                _property.value = ArrayList()
            }
        }
    }
```
## 第 2 步：為狀態ImageView添加一個綁定適配器
在此步驟中，使用ImageView連接到數據綁定的 來顯示加載和錯誤狀態的圖標。<br/>當應用程序處於加載狀態或錯誤狀態時，ImageView應該是可見的。當應用程序加載完成後，ImageView應該是不可見的。

1. 打開BindingAdapters.kt。添加一個名為的新綁定適配器bindStatus()，它將一個ImageView和一個GarbageTruckApiStatus值作為參數。
2. when {}在bindStatus()方法內部添加一個在不同狀態之間切換的方法。

3. 在when {}中，為加載狀態 (GarbageTruckApiStatus.LOADING)添加一個案例。<br/>對於此狀態，將設置ImageView為可見，並為其分配加載動畫。

4. 為錯誤狀態添加一個案例，即GarbageTruckApiStatus.ERROR。<br/>
與您為LOADING狀態所做的類似，將狀態設置ImageView為可見並重用連接錯誤可繪製對象。

5. 為完成狀態添加一個案例，即GarbageTruckApiStatus.DONE. <br/>
在這裡有一個成功的響應，所以關閉狀態的可見性ImageView以隱藏它。

```kotlin
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
```
## 第 3 步：在佈局中添加狀態 ImageView
1. 打開 fragment_garbage_truck.xml。在RecyclerView元素下方，在 中ConstraintLayout，添加ImageView如下所示。

這ImageView與RecyclerView. 但是，寬度和高度用於wrap_content使圖像居中，而不是拉伸圖像以填充視圖。還要注意app:garbageTruckApiStatus屬性，BindingAdapter當視圖模型中的狀態屬性更改時，視圖會調用您的屬性。
```xml
<ImageView
    android:id="@+id/status_image"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:garbageTruckApiStatus="@{viewModel.status}" />
```   
2. 在模擬器或設備中打開飛行模式以模擬丟失的網絡連接。編譯並運行應用程序，注意到出現錯誤圖像。
---

# 創建詳細頁面並設置導航 <br/>Create a detail page and set up navigation

## 第 1步 ：Create the detail view model and update detail layout
1. 創建 GarbageTruckDetailFragemet.kt 詳細頁面，並設置<br/>
navigation 導航

2. 打開detail/GarbageTruckViewModel.kt。
```kotlin
class GarbageTruckDetailViewModel(garbageTruckProperty: GarbageTruckProperty,app: Application) : AndroidViewModel(app) {
    /**
     * 在類定義中，LiveData為選定的 GarbageTruckProperty 屬性添加，以將該信息公開給詳細信息視圖。
     * 按照通常的模式創建一個MutableLiveData來保存它GarbageTruckProperty本身，
     * 然後公開一個不可變的公共LiveData屬性。
     */
    private val _selectedProperty = MutableLiveData<GarbageTruckProperty>()
    val selectedProperty: LiveData<GarbageTruckProperty>
    get() = _selectedProperty

    //創建一個init {}塊並使用構造函數中的對象設置所選 GarbageTruckProperty 屬性的值。
    init {
        _selectedProperty.value = garbageTruckProperty
    }
}
```
3. 打開res/layout/fragment_garbate_truck_detail.xml，設計視圖畫面打並添加一個<data>元素來將詳細視圖模型與佈局相關聯。
4. 將app:屬性添加到TextView元素。

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="viewModel"
            type="com.example.retrofitroom_taipeigarbagetruck.ui.detail.GarbageTruckDetailViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".ui.detail.GarbageTruckDetailFragment">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:layout_marginTop="16dp"
            android:layout_marginEnd="16dp"
            android:orientation="vertical"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/tv_Admin_District"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:padding="2dp"
                    android:text="@{viewModel.selectedProperty.admin_District}"
                    tools:text="中山區" />

                <TextView
                    android:id="@+id/tv_Village"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:padding="2dp"
                    android:text="@{viewModel.selectedProperty.village}"
                    tools:text="力行里" />

                <TextView
                    android:id="@+id/tv_Branch"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:padding="2dp"
                    android:text="@{viewModel.selectedProperty.branch}"
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
                    android:text="@{viewModel.selectedProperty.latitude}"
                    tools:text="25.05111111" />

                <TextView
                    android:id="@+id/tv_longitude"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@{viewModel.selectedProperty.longitude}"
                    tools:text="121.5369444" />

            </LinearLayout>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:padding="2dp">

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
                    android:text="@{viewModel.selectedProperty.arrival_Time}"
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
                    android:text="@{viewModel.selectedProperty.departure_Time}"
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
                    android:text="@{viewModel.selectedProperty.location}"
                    android:textColor="@color/gray"
                    tools:text="臺北市中山區建國北路一段69號前" />
            </LinearLayout>

        </LinearLayout>
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```
## 第 2 步：在GarvageTruckViewModel中定義導航

當用戶點擊GarvageTruckViewModel時，<br/>它應該觸發導航到一個片段，該片段顯示有關"單擊"項目的詳細信息。

1. 打開ui/GarvageTruckViewModel.kt。 <br/> 添加一個_navigateToSelectedProperty MutableLiveData屬性並使用不可變的LiveData.
```kotlin
 // 當用戶點擊recyclerview時，它應該觸發導航到一個片段，該片段顯示有關單擊項目的詳細信息。
    // LiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<GarbageTruckProperty>()
    val navigateToSelectedProperty: LiveData<GarbageTruckProperty>
        get() = _navigateToSelectedProperty
```
2. 在類的末尾，添加一個displayPropertyDetails() <br/>將 _navigateToSelectedProperty設置為所選屬性的方法。
```kotlin
    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [GarbageTruckProperty] that was clicked on.
     */
    fun displayPropertyDetails(garbageTruckProperty: GarbageTruckProperty){
        _navigateToSelectedProperty.value = garbageTruckProperty
    }
```
3. 添加一個displayPropertyDetailsComplete() 將值歸零的方法_navigateToSelectedProperty。<br/>需要它來標記導航狀態完成，並避免在用戶從詳細信息視圖返回時再次觸發導航。
```kotlin
 /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
```
 ## 第 3 步：在GarbageTruckAdapter 和片段中設置點擊偵聽器 
  1. 打開GarbageTruckAdapter.kt。在類的末尾，創建一個自定義OnClickListener類，該類採用帶GarbageTruckProperty參數的 lambda 。在類中，定義一個onClick()設置為 lambda 參數的函數。
  ```kotlin
  /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [GarbageTruckProperty]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [GarbageTruckProperty]
     */
    class OnClickListener(val clickListener: (garbageTruckProperty: GarbageTruckProperty) -> Unit){
        fun onClick(garbageTruckProperty: GarbageTruckProperty) = clickListener(garbageTruckProperty)
    }
```
 2. 向上滾動到類定義GarbageTruckAdapter，並將私有OnClickListener屬性添加到構造函數。 
 ```kotlin
 class GarbageTruckAdapter (private val onClicklistener: OnClickListener): ListAdapter<GarbageTruckProperty
        , GarbageTruckAdapter.GarbageTruckViewHolder>(DiffCallback) {
```
3. 通過向方法中onClickListener的添加 使可點擊onBindviewHolder()。在對調用之間定義點擊偵聽器getItem() and bind()。
```kotlin
 override fun onBindViewHolder(holder: GarbageTruckViewHolder, position: Int) {
        val garbageTruckProperty = getItem(position)

        holder.itemView.setOnClickListener {
            onClicklistener.onClick(garbageTruckProperty)
        }
        holder.bind(garbageTruckProperty)
    }
```
4. 打開GarbageTruckFragment.kt。在該onCreateView()方法中，將初始化binding.recycler.adapter屬性的行替換為如下所示的行。 
```kotlin
 // Sets the adapter of the recycler RecyclerView with clickHandler lambda that
// tells the viewModel when our property is clicked
    binding.recycler.adapter = GarbageTruckAdapter(GarbageTruckAdapter.OnClickListener{
        viewModel.displayPropertyDetails(it)
    })
```
## 第 4 步：修改導航圖並使 GarbageTruckProperty 可分塊
當用戶點擊RecyclerView的單一Item時，應用程序應導航到詳細信息片段<br/>並傳遞所選 GarbageTruck 屬性的詳細信息，以便詳細信息視圖可以顯示該信息。

現在你有一個點擊監聽器GaarbageTruckAdapter來處理點擊，以及一種從視圖模型觸發導航的方法。<br/>但是您還沒有將 GarbageTruckProperty對像傳遞給詳細信息片段。為此，您可以使用導航組件中的 Safe Args。

1. 打開res/navigation/navigation.xml。單擊“文本”選項卡以查看導航圖的 XML 代碼。
2. 在<fragment>細節片段的<argument>元素內，添加如下所示的元素。這個名為selectedProperty的參數具有類型 GarbageTuckProperty。

```xml
 <fragment
        android:id="@+id/garbageTruckDetailFragment"
        android:name="com.example.retrofitroom_taipeigarbagetruck.ui.detail.GarbageTruckDetailFragment"
        android:label="fragment_garbage_truck_detail"
        tools:layout="@layout/fragment_garbage_truck_detail" >

        <argument
            android:name="selectedProperty"
            app:argType="com.example.retrofitroom_taipeigarbagetruck.network.GarbageTruckProperty"/>

    </fragment>
``` 
 3. 編譯應用程序。Navigation 給你一個錯誤，因為GarbateTruckProperty它不是Parcelable。<br/>
 該Parcelable接口使對象能夠被序列化，以便對象的數據可以在片段或活動之間傳遞。<br/>
 在這種情況下，要GarbageTruckProperty通過 Safe Args將對象內部的數據傳遞給詳細信息片段，GarbageTruckProperty必須實現該Parcelable接口。好消息是 Kotlin 為實現該接口提供了一個簡單的捷徑。 

 4. 打開GarbageTruckProperty.kt。將@Parcelize註釋添加到類定義中。請求時導入kotlinx.parcel.Parcelize。<br/>
 若無法import請到 build.grade(app) 加中上 plugins id 'kotlin-parcelize'<br/>
```kotlin
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
    .....
) : Parcelable
```
## 第 5 步：連接片段 Connect the fragments
仍然沒有導航——實際導航發生在片段中。在此步驟中，您添加用於在概覽和詳細信息片段之間實現導航的最後幾位。

1. 打開ui/GarbageTruckFragment.kt。在onCreateView()中，在初始化GarbageTruckAdapter的行下方，添加如下所示的行以navigatedToSelectedProperty從概覽視圖模型中觀察。<br/>
需導入androidx.lifecycle.Observer和 導入 androidx.navigation.fragment.findNavController。<br/>
觀察者測試GarbagetruckProperty-it在 lambda 中 - 是否不為空，如果是，它從帶有findNavController().<br/> 
調用displayPropertyDetailsComplete()以告訴視圖模型將 重置LiveData為空狀態，這樣當應用程序返回到GarbageTruckFragment.

```kotlin
  // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if (null != it){
                this.findNavController().navigate(
                    GarbageTruckFragmentDirections.actionToGarbageTruckDetailFragment(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })
```
2. 打開detail/GarbageTruckDetailFragment.kt。binding.lifecycleOwner 在onCreateView()方法中設置屬性的正下方添加此行。
此行GarbageTruckProperty從 Safe Args 中獲取選定的對象。 

>請注意 Kotlin 的非空斷言運算符 ( !!) 的使用。如果selectedProperty不存在，則發生了可怕的事情，您實際上希望代碼拋出一個空指針。（在生產代碼中，您應該以某種方式處理該錯誤。）  
```kotlin
// 此行從 GarbageTruckProperty Safe Args 中獲取選定的對象。
 val garbageTruckProperty =
 GarbageTruckDetailFragmentArgs.fromBundle(arguments!!).selectedProperty  
 ```
 3. 接下來添加這一行，以獲得一個新的GarbageTruckDetailViewModelFactory.<br/> 
 您將使用GarbageTruckDetailViewModelFactory來獲取實例GarbageTruckDetailViewModel。<br/>入門應用程序包含實現GarbageTruckDetailViewModelFactory，因此您在這裡要做的就是初始化它。 
 ```kotlin
class GarbageTruckDetailViewModelFactory(
    private val garbageTruckProperty: GarbageTruckProperty,
    private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageTruckDetailViewModel::class.java)){
            return GarbageTruckDetailViewModel(garbageTruckProperty, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
 ```  
 ```kotlin
  val application = requireNotNull(activity).application

  val viewModelFactory = GarbageTruckDetailViewModelFactory(garbageTruckProperty, application)
``` 
4. 最後，添加這條線以DetailViewModel從工廠獲得一個並連接所有部件。
```kotlin
 binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(GarbageTruckDetailViewModel::class.java)
```
5. 編譯並運行該應用程序，然後點擊任何Recycler item。顯示該屬性的詳細信息的詳細信息片段。點擊返回按鈕返回概覽頁面。  
---
<br/>
<br/>

# Android Kotlin 基礎：存儲庫 Repository

將通過使用離線緩存來改善應用的用戶體驗。許多應用程序依賴於來自網絡的數據。如果您的應用程序在每次啟動時從服務器獲取數據，用戶可能會看到加載屏幕，這可能是一種糟糕的用戶體驗。用戶可能會卸載您的應用。

當用戶啟動應用程序時，他們希望應用程序能夠快速顯示數據。您可以通過實施離線緩存來實現此目標。離線緩存意味著您的應用程序將從網絡獲取的數據保存在設備的本地存儲上，以便更快地訪問。

許多用戶間歇性地訪問互聯網。通過實施離線緩存，您可以為您的應用添加離線支持，幫助這些用戶在離線時使用您的應用。

## 第 1 步：調整 package 的位置
1. 將原本在 network/GarbageTruckProperty，移轉到新建的package
domain/GarbageTruckProperty.kt

2. 將原本在 ui/GarbageTruckViewModel，移轉到新建的package
viewmodels/GarbageTruckViewModel
---

# 添加離線緩存 Add an offline cache
## 第 1 步：添加 Room 依賴項
1. 打開build.gradle (Module:app)文件並將Room依賴項添加到項目中。
```kotlin
// Room Database dependency
    def room_version = "2.3.0"
    implementation "androidx.room:room-runtime:$room_version"
    // Kotlin Extensions and Coroutines support for Room
    implementation "androidx.room:room-ktx:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"
```
## 第 2 步：添加數據庫對象
在此步驟中，您將創建一個命名為 DatabaseGarbageTruck 表示數據庫對象的數據庫實體。<br/>
還可以實現將DatabaseGarbageTruck對象轉換為(domain)域對像以及將網絡對象轉換為DatabaseGarbageTruck對象的便捷方法。

1. 創建database/DatabaseEntities.kt並創建一個Room名為的實體DatabaseGarbageTruck。
```kotlin
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
```
2. 在database/DatabaseEntities.kt中，創建一個名為asDomainModel() 的擴展函數。使用該函數將DatabaseGarbageTruck數據庫對象轉換為域對象。
```kotlin
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
```
在這個應用程序中，轉換很簡單，其中一些代碼不是必需的。但在實際應用中( domain, database, and network objects )，域、數據庫和網絡對象的結構會有所不同。

3. 創建 network/DataTransferObjects.kt並創建一個名為asDatabaseModel(). 使用該函數將網絡對象轉換為DatabaseGarbateTruck數據庫對象。
```kotlin
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
```
## 第 3 步：添加 GarbageTruckDao
在此步驟中，將實現GarbageTruckDao並定義兩個幫助程序方法來訪問數據庫。一種輔助方法從數據庫中獲取資料，另一種方法插入數據庫。
1. 創建 database/Room.kt，定義一個GarbageTruckDao接口並用 進行註釋@Dao。
```kotlin
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
```
>CRUD 增刪查改都是透過這個介面來完成，
@Insert(onConflict = OnConflictStrategy.REPLACE) 表示新增物件時和舊物件發生衝突後的處置：<br/>
REPLACE 蓋掉 (最常用)<br/>
ROLLBACK 閃退<br/>
ABORT 閃退 (默認)<br/>
FAIL 閃退<br/>
IGNORE 忽略，還是舊的資料<br/>

## 第 4 步：實施 RoomDatabase
1. 在database/Room.kt中，interface GarbageTruckDao之後，創建一個abstract名為GarbageTrucksDatabase 的類 。擴展GarbageTrucksDatabase的RoomDatabase。
2. 在GarbageTrucksDatabase裡面，定義一個類型的變量garbageTruckDao來訪問Dao方法。
```kotlin
// 使用@Database註釋將GarbageTrucksDatabase類標記為Room數據庫。
// 聲明DatabaseGarbageTruck屬於該數據庫的實體，並將版本號設置為1。
@Database(entities = [DatabaseGarbageTruck::class] , version = 1)
abstract class GarbageTrucksDatabase: RoomDatabase(){
    abstract val garbageTruckDao: GarbageTruckDao
}
```
4. 創建一個在類外部private lateinit調用的變量INSTANCE，以保存單例對象。該GarbageTruckDatabase應 singleton，防止發生在同一時間打開數據庫的多個實例。

5. getDatabase()在類之外創建和定義一個方法。在 中getDatabase()，初始化並返回塊INSTANCE內的變量synchronized。

```kotlin

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
```
