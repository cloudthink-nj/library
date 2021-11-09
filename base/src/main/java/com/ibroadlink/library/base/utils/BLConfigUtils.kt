package com.ibroadlink.library.base.utils

import android.provider.Settings
import com.blankj.utilcode.util.*
import com.ibroadlink.library.base.app.BaseApplication.Companion.appContext
import com.ibroadlink.library.base.app.BaseApplication.Companion.mmkv
import com.ibroadlink.library.base.app.BaseViewModel
import com.ibroadlink.library.base.extend.launch
import com.ibroadlink.library.base.extend.parseArray
import kotlinx.coroutines.delay
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.InputStreamReader
import java.util.*

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/8/23 16:22
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: 项目配置信息
 */
object BLConfigUtils : BaseViewModel() {

    var configProp: Properties? = null
        private set
    private const val PROJECT_CONFIG_FILE = "appConfig.properties"

    fun init() {
        var confContent = BLZipUtils.readComment()
        LogUtils.dTag("ConfigUtils", "readComment = $confContent")
        if (confContent.isNullOrBlank()) {
            confContent = ResourceUtils.readAssets2String(PROJECT_CONFIG_FILE, "utf-8")
            LogUtils.dTag("ConfigUtils", "appConfig = $confContent")
        }
        if (!confContent.isNullOrBlank()) {
            val inputStream = ByteArrayInputStream(confContent.toByteArray())
            val isr = InputStreamReader(inputStream, Charsets.UTF_8)
            configProp = Properties()
            try {
                configProp?.load(isr)
            } catch (e: Exception) {
                LogUtils.e(e.message)
            }
            closeQuietly(inputStream)
            closeQuietly(isr)
        }

        launch({
            delay(BLConstantUtils.TWO_SECOND_DURATION)
            val lastVersion = mmkv.decodeInt("app_last_version")
            if (AppUtils.getAppVersionCode() > lastVersion) {
                mmkv.encode("app_last_version", AppUtils.getAppVersionCode())
                ResourceUtils.copyFileFromAssets(
                    "rings",
                    PathUtils.getExternalRingtonesPath()
                )
                configProp?.run {
                    stringPropertyNames().forEach { key ->
                        getProperty(key)?.let { value ->
                            setSystemDB(key, value)
                            delay(20)
                        }
                    }
                }
            }
        })
    }

    inline fun <reified T> getList(
        key: String,
        defaultValue: List<T> = emptyList()
    ): List<T> {
        val value = getString(key)
        LogUtils.dTag("ConfigUtils", "key=$key, value=$value")
        return value.parseArray() ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val value = getString(key)
        LogUtils.dTag("ConfigUtils", "key=$key, value=$value")
        return value?.toInt() ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val value = getString(key)
        LogUtils.dTag("ConfigUtils", "key=$key, value=$value")
        return value?.toBoolean() ?: defaultValue
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        var value = defaultValue
        val dbValue = getSystemDB(key)
        if (!dbValue.isNullOrBlank()) {
            value = dbValue
        } else {
            val propValue = configProp?.getProperty(key)
            if (!propValue.isNullOrBlank()) {
                value = propValue
            }
        }
        LogUtils.dTag("ConfigUtils", "key=$key, value=$value")
        return value
    }

    private fun setSystemDB(key: String, value: String) {
        Settings.System.putString(appContext.contentResolver, key, value)
    }

    private fun getSystemDB(key: String): String? {
        return Settings.System.getString(appContext.contentResolver, key)
    }

    private fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }

}