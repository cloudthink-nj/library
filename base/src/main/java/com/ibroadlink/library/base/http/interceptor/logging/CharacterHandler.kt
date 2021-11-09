package com.ibroadlink.library.base.http.interceptor.logging

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * 描述　:
 */
object CharacterHandler {

    fun jsonFormat(json: String): String {
        var json = json
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content"
        }
        var message: String
        try {
            json = json.trim { it <= ' ' }
            message = when {
                json.startsWith("{") -> {
                    val jsonObject = JSONObject(json)
                    jsonObject.toString(4)
                }
                json.startsWith("[") -> {
                    val jsonArray = JSONArray(json)
                    jsonArray.toString(4)
                }
                else -> {
                    json
                }
            }
        } catch (e: JSONException) {
            message = json
        } catch (error: OutOfMemoryError) {
            message = "Output omitted because of Object size"
        }
        return message
    }

    /**
     * xml 格式化
     *
     * @param xml
     * @return
     */
    fun xmlFormat(xml: String?): String? {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content"
        }
        return try {
            val xmlInput: Source =
                StreamSource(StringReader(xml))
            val xmlOutput =
                StreamResult(StringWriter())
            val transformer =
                TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
        } catch (e: TransformerException) {
            xml
        }
    }
}