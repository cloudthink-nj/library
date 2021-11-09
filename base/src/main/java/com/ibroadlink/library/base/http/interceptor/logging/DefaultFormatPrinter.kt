package com.ibroadlink.library.base.http.interceptor.logging

import android.text.TextUtils
import com.ibroadlink.library.base.http.interceptor.logging.LogInterceptor.Companion.isJson
import com.ibroadlink.library.base.http.interceptor.logging.LogInterceptor.Companion.isXml
import com.blankj.utilcode.util.LogUtils
import com.ibroadlink.library.base.http.interceptor.logging.CharacterHandler.jsonFormat
import com.ibroadlink.library.base.http.interceptor.logging.CharacterHandler.xmlFormat
import okhttp3.MediaType
import okhttp3.Request

/**
 * 描述　:
 */
class DefaultFormatPrinter : FormatPrinter {
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 可以解析的情况
     *
     * @param request
     * @param bodyString
     */
    override fun printJsonRequest(
        request: Request,
        bodyString: String
    ) {
        val requestBody = BODY_TAG + LINE_SEPARATOR + bodyString
        val tag = getTag(true)
        val stringBuilder = StringBuilder()
        stringBuilder.append(REQUEST_UP_LINE)
        appendLines(
            stringBuilder,
            arrayOf(URL_TAG + request.url())
        )
        appendLines(
            stringBuilder,
            getRequest(request)
        )
        appendLines(
            stringBuilder,
            requestBody.split(LINE_SEPARATOR!!).toTypedArray()
        )
        stringBuilder.append(END_LINE)
        LogUtils.dTag(tag, stringBuilder.toString())
    }

    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 为 `null` 或不可解析的情况
     *
     * @param request
     */
    override fun printFileRequest(request: Request) {
        val tag = getTag(true)
        val stringBuilder = StringBuilder()
        stringBuilder.append(REQUEST_UP_LINE)
        appendLines(
            stringBuilder,
            arrayOf(URL_TAG + request.url())
        )
        appendLines(
            stringBuilder,
            getRequest(request)
        )
        stringBuilder.append(OMITTED_REQUEST)
        stringBuilder.append(END_LINE)
        LogUtils.dTag(tag, stringBuilder.toString())
    }

    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 可以解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param contentType  服务器返回数据的数据类型
     * @param bodyString   服务器返回的数据(已解析)
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    override fun printJsonResponse(
        chainMs: Long,
        isSuccessful: Boolean,
        code: Int,
        headers: String,
        contentType: MediaType?,
        bodyString: String?,
        segments: List<String?>,
        message: String,
        responseUrl: String
    ) {
        var bodyString = bodyString
        bodyString =
            when {
                isJson(contentType) -> jsonFormat(bodyString!!)
                isXml(
                    contentType
                ) -> xmlFormat(bodyString)
                else -> bodyString
            }
        val responseBody = BODY_TAG + LINE_SEPARATOR + bodyString
        val tag = getTag(false)
        val urlLine = arrayOf<String?>(
            URL_TAG + responseUrl
        )
        val stringBuilder = StringBuilder()
        stringBuilder.append(RESPONSE_UP_LINE)
        appendLines(
            stringBuilder,
            urlLine
        )
        appendLines(
            stringBuilder,
            getResponse(
                headers,
                chainMs,
                code,
                isSuccessful,
                segments,
                message
            )
        )
        appendLines(
            stringBuilder,
            responseBody.split(LINE_SEPARATOR!!).toTypedArray()
        )
        stringBuilder.append(END_LINE)
        LogUtils.dTag(tag, stringBuilder.toString())
    }

    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 为 `null` 或不可解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    override fun printFileResponse(
        chainMs: Long,
        isSuccessful: Boolean,
        code: Int,
        headers: String,
        segments: List<String?>,
        message: String,
        responseUrl: String
    ) {
        val tag = getTag(false)
        val urlLine = arrayOf<String?>(
            URL_TAG + responseUrl
        )

        val stringBuilder = StringBuilder()
        stringBuilder.append(RESPONSE_UP_LINE)
        appendLines(
            stringBuilder,
            urlLine
        )
        appendLines(
            stringBuilder,
            getResponse(
                headers,
                chainMs,
                code,
                isSuccessful,
                segments,
                message
            )
        )
        stringBuilder.append(OMITTED_RESPONSE)
        stringBuilder.append(END_LINE)
        LogUtils.dTag(tag, stringBuilder.toString())
    }

    companion object {
        private const val TAG = "Http"
        private val LINE_SEPARATOR = System.getProperty("line.separator")
        private val DOUBLE_SEPARATOR =
            LINE_SEPARATOR + LINE_SEPARATOR
        private const val N = "\n"
        private const val T = "\t"
        private const val REQUEST_UP_LINE =
            "┌────── Request ────────────────────────────────────────────────────────────────────────$N"
        private const val END_LINE =
            "└───────────────────────────────────────────────────────────────────────────────────────$N"
        private const val RESPONSE_UP_LINE =
            "┌────── Response ───────────────────────────────────────────────────────────────────────$N"
        private const val BODY_TAG = "Body:"
        private const val URL_TAG = "URL: "
        private const val METHOD_TAG = "Method: @"
        private const val HEADERS_TAG = "Headers:"
        private const val STATUS_CODE_TAG = "Status Code: "
        private const val RECEIVED_TAG = "Received in: "
        private const val CORNER_UP = "┌ "
        private const val CORNER_BOTTOM = "└ "
        private const val CENTER_LINE = "├ "
        private const val DEFAULT_LINE = "│ "
        private const val OMITTED_RESPONSE = DEFAULT_LINE + "Omitted response body$N"
        private const val OMITTED_REQUEST = DEFAULT_LINE + "Omitted request body$N"
        private val ARMS =
            arrayOf("-A-", "-R-", "-M-", "-S-")
        private val last: ThreadLocal<Int> = object : ThreadLocal<Int>() {
            override fun initialValue(): Int {
                return 0
            }
        }

        private fun isEmpty(line: String): Boolean {
            return TextUtils.isEmpty(line) || N == line || T == line || TextUtils.isEmpty(
                line.trim { it <= ' ' }
            )
        }

        /**
         * 对 `lines` 中的信息进行逐行打印
         *
         * @param tag
         * @param lines
         */
        private fun appendLines(
            stringBuilder: StringBuilder,
            lines: Array<String?>
        ) {
            for (line in lines) {
                stringBuilder.append(DEFAULT_LINE + line + N)
            }
        }

        private fun computeKey(): String {
            if (last.get()!! >= 4) {
                last.set(0)
            }
            val s =
                ARMS[last.get()!!]
            last.set(last.get()!! + 1)
            return s
        }

        private fun getRequest(request: Request): Array<String?> {
            val log: String
            val header = request.headers().toString()
            log =
                METHOD_TAG + request.method() + DOUBLE_SEPARATOR +
                        if (isEmpty(header)) "" else HEADERS_TAG + LINE_SEPARATOR + dotHeaders(
                            header
                        )
            return log.split(LINE_SEPARATOR!!).toTypedArray()
        }

        private fun getResponse(
            header: String, tookMs: Long, code: Int, isSuccessful: Boolean,
            segments: List<String?>, message: String
        ): Array<String?> {
            val log: String
            val segmentString =
                slashSegments(segments)
            log =
                ((if (!TextUtils.isEmpty(segmentString)) "$segmentString - " else "") + "is success : "
                        + isSuccessful + " - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
                        code + " / " + message + DOUBLE_SEPARATOR + if (isEmpty(
                        header
                    )
                ) "" else HEADERS_TAG + LINE_SEPARATOR +
                        dotHeaders(header))
            return log.split(LINE_SEPARATOR!!).toTypedArray()
        }

        private fun slashSegments(segments: List<String?>): String {
            val segmentString = StringBuilder()
            for (segment in segments) {
                segmentString.append("/").append(segment)
            }
            return segmentString.toString()
        }

        /**
         * 对 `header` 按规定的格式进行处理
         *
         * @param header
         * @return
         */
        private fun dotHeaders(header: String): String {
            val headers =
                header.split(LINE_SEPARATOR!!).toTypedArray()
            val builder = StringBuilder()
            var tag = "─ "
            if (headers.size > 1) {
                for (i in headers.indices) {
                    tag = when (i) {
                        0 -> {
                            CORNER_UP
                        }
                        headers.size - 1 -> {
                            CORNER_BOTTOM
                        }
                        else -> {
                            CENTER_LINE
                        }
                    }
                    builder.append(tag).append(headers[i]).append("\n")
                }
            } else {
                for (item in headers) {
                    builder.append(tag).append(item).append("\n")
                }
            }
            return builder.toString()
        }

        private fun getTag(isRequest: Boolean): String {
            return if (isRequest) {
                "$TAG-Request"
            } else {
                "$TAG-Response"
            }
        }
    }
}