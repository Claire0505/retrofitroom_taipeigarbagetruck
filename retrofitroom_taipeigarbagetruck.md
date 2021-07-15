# UsingRetrofitRoom TaipeiGarbageTruck

## 第 1 步：在 build.gradle (app)中添加導航片段和數據綁定相關依賴庫
1. 啟用DataBinding (要使用數據綁定，需要使用< layout >標籤包裝XML佈局)
```
// 啟用DataBinding (要使用數據綁定，需要使用<layout>標籤包裝XML佈局)
    buildFeatures {
        dataBinding true
    }
```
2. 在dependencies{ } 添加 Navigation
```
// navigation
def nav_version = "2.3.5"

implementation
("androidx.navigation:navigation-fragment-ktx:$nav_version")

implementation
("androidx.navigation:navigation-ui-ktx:$nav_version")   
```
3. Navigation 使用 Safe Args 傳遞類型安全的數據
```
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
```
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
