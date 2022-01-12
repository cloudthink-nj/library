/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/19 14:35
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: xxx
 */
object LibraryDeps {

    /**
     * AndroidX相关依赖
     */
    const val AndroidJUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val AppCompat = "androidx.appcompat:appcompat:${Versions.AppCompat}"
    const val CoreKtx = "androidx.core:core-ktx:${Versions.CoreKtx}"
    const val ReflectKtx = "org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}"
    const val ConstraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.ConstraintLayout}"
    const val TestExtJunit = "androidx.test.ext:junit:${Versions.TestExtJunit}"
    const val TestEspresso = "androidx.test.espresso:espresso-core:${Versions.TestEspresso}"
    const val MultiDex = "androidx.multidex:multidex:${Versions.MultiDex}"
    const val FragmentKtx = "androidx.fragment:fragment-ktx:${Versions.Fragment}"

    /**
     * Android相关依赖
     *
     */
    const val Junit = "junit:junit:${Versions.Junit}"
    const val Material = "com.google.android.material:material:${Versions.Material}"

    /**
     * JetPack相关依赖
     *
     */
    const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Lifecycle}"
    const val LiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Lifecycle}"
    const val Lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.Lifecycle}"
    const val RoomKTX = "androidx.room:room-ktx:${Versions.Room}"
    const val RoomCompiler = "androidx.room:room-compiler:${Versions.Room}"

    /**
     * 第三方相关依赖
     *
     */
    const val Leakcanary = "com.squareup.leakcanary:leakcanary-android:2.6"
    const val DebugDB = "com.github.amitshekhariitbhu:Android-Debug-Database:1.0.6"

    const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp}"
    const val Retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit}"
    const val Converter = "com.squareup.retrofit2:converter-gson:${Versions.Retrofit}"

    //微信开源项目，替代SP https://github.com/Tencent/MMKV
    const val MMKV = "com.tencent:mmkv-static:${Versions.MMKV}"

    //coil https://github.com/coil-kt/coil
    const val Coil = "io.coil-kt:coil:${Versions.Coil}"

    //https://github.com/Blankj/AndroidUtilCode com.blankj:utilcodex:1.30.6
    const val UtilCodex = "com.blankj:utilcodex:${Versions.UtilCodex}"

    //屏幕适配 https://github.com/JessYanCoding/AndroidAutoSize
    const val AutoSize = "com.github.JessYanCoding:AndroidAutoSize:${Versions.AutoSize}"

    //Fragivity https://github.com/vitaviva/fragivity
    const val Fragivity = "com.github.vitaviva.fragivity:core:${Versions.Fragivity}"

    const val EzvizSdk = "io.github.ezviz-open:ezviz-sdk:${Versions.EzvizSdk}"

    //XPopup https://github.com/li-xiaojun/XPopup
    const val XPopup = "com.github.li-xiaojun:XPopup:${Versions.XPopup}"

    //BaseAdapter
    const val BRVAH = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.6"

    //lottie 动画效果
    const val Lottie = "com.airbnb.android:lottie:3.4.0"

    //dueros  https://github.com/dueros/AndroidBotSdkDemo
    const val FastJson = "com.alibaba:fastjson:1.1.71.android"
    const val Dueros = "com.baidu.duer.botsdk:bot-sdk-android:1.51.1"

    //webServer https://github.com/NanoHttpd/nanohttpd
    const val NanoHttpd = "org.nanohttpd:nanohttpd:2.3.1"
    const val FileUpload = "org.nanohttpd:nanohttpd-apache-fileupload:2.3.1"

    //https://github.com/hackware1993/MagicIndicator
    const val MagicIndicator = "com.github.hackware1993:MagicIndicator:1.7.0"
}