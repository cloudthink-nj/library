# natvie 方法不混淆
-keepclasseswithmembernames class * {
native <methods>;
}

-keep class com.ibroadlink.library.aidl.IAidlCallback { *; }
-keep class com.ibroadlink.library.aidl.IAidlService { *; }
-dontwarn com.ibroadlink.library.aidl.**