-dontwarn com.ibroadlink.library.aidlink.adapter.**
-keep class * implements com.ibroadlink.library.aidlink.SuperParcelable {
    public void readFromParcel(android.os.Parcel);
}
-keep @com.ibroadlink.library.aidlink.annotation.RemoteInterface class * {
    <methods>;
}