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
