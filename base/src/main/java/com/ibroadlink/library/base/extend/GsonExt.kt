package com.ibroadlink.library.base.extend

import com.blankj.utilcode.util.LogUtils
import com.google.gson.*
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.math.ceil


val GSON: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(
            object : TypeToken<Map<String?, Any?>?>() {}.type,
            MapDeserializerDoubleAsIntFix()
        )
        .registerTypeAdapter(Int::class.java, IntJsonDeserializer())
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
}

/**
 * 将对象转为JSON字符串
 */
fun Any?.toJsonString(): String {
    return GSON.toJson(this)
}

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

inline fun <reified T> String?.parseObject(): T? {//可转成任意类型
    return kotlin.runCatching {
        GSON.fromJson(this, genericType<T>()) as? T
    }.onFailure {
        LogUtils.e("###JSON解析出错，类名：${T::class.java.simpleName} ###，字符串是：" + this)
    }.getOrNull()
}

inline fun <reified T> String?.parseArray(): List<T>? {
    return kotlin.runCatching {
        GSON.fromJson(this, ParameterizedTypeImpl(T::class.java)) as? List<T>
    }.onFailure {
        LogUtils.e("###JSON解析出错，类名：${T::class.java.simpleName} ###，字符串是：" + this)
    }.getOrNull()
}

class ParameterizedTypeImpl(private val clazz: Class<*>) : ParameterizedType {
    override fun getRawType(): Type = List::class.java

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)
}

/**
 * int类型转化失败时跳过
 */
class IntJsonDeserializer : JsonDeserializer<Int?> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int? {
        return when {
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                if (prim.isNumber) {
                    prim.asNumber.toInt()
                } else {
                    null
                }
            }
            else -> null
        }
    }
}


/**
 * 修复Int变为Double的问题
 */
class MapDeserializerDoubleAsIntFix :
    JsonDeserializer<Map<String, Any?>?> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): Map<String, Any?>? {
        @Suppress("unchecked_cast")
        return read(jsonElement) as? Map<String, Any?>
    }

    fun read(json: JsonElement): Any? {
        when {
            json.isJsonArray -> {
                val list: MutableList<Any?> = ArrayList()
                val arr = json.asJsonArray
                for (anArr in arr) {
                    list.add(read(anArr))
                }
                return list
            }
            json.isJsonObject -> {
                val map: MutableMap<String, Any?> =
                    LinkedTreeMap()
                val obj = json.asJsonObject
                val entitySet =
                    obj.entrySet()
                for ((key, value) in entitySet) {
                    map[key] = read(value)
                }
                return map
            }
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                when {
                    prim.isBoolean -> {
                        return prim.asBoolean
                    }
                    prim.isString -> {
                        return prim.asString
                    }
                    prim.isNumber -> {
                        val num: Number = prim.asNumber
                        // here you can handle double int/long values
                        // and return any type you want
                        // this solution will transform 3.0 float to long values
                        return if (ceil(num.toDouble()) == num.toLong().toDouble()) {
                            num.toLong()
                        } else {
                            num.toDouble()
                        }
                    }
                }
            }
        }
        return null
    }
}