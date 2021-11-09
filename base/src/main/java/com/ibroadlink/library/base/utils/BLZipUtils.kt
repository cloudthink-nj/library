package com.ibroadlink.library.base.utils

import com.blankj.utilcode.util.Utils
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*
import java.util.zip.*

/**
 * 描述　:
 */
object BLZipUtils {

    fun decompressToStringForZlib(
        bytesToDecompress: ByteArray,
        charsetName: String = "UTF-8"
    ): String? {
        val bytesDecompressed = decompressForZlib(bytesToDecompress)
        var returnValue: String? = null
        try {
            returnValue =
                String(bytesDecompressed, 0, bytesDecompressed.size, Charset.forName(charsetName))
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        }
        return returnValue
    }

    /**
     * zlib decompress 2 byte
     *
     * @param bytesToDecompress
     * @return
     */
    private fun decompressForZlib(bytesToDecompress: ByteArray): ByteArray {
        var returnValues: ByteArray = byteArrayOf()
        val inflater = Inflater()
        val numberOfBytesToDecompress = bytesToDecompress.size
        inflater.setInput(
            bytesToDecompress,
            0,
            numberOfBytesToDecompress
        )
        var numberOfBytesDecompressedSoFar = 0
        val bytesDecompressedSoFar: MutableList<Byte> =
            ArrayList()
        try {
            while (!inflater.needsInput()) {
                val bytesDecompressedBuffer =
                    ByteArray(numberOfBytesToDecompress)
                val numberOfBytesDecompressedThisTime = inflater.inflate(
                    bytesDecompressedBuffer
                )
                numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime
                for (b in 0 until numberOfBytesDecompressedThisTime) {
                    bytesDecompressedSoFar.add(bytesDecompressedBuffer[b])
                }
            }
            returnValues = ByteArray(bytesDecompressedSoFar.size)
            for (b in returnValues.indices) {
                returnValues[b] = bytesDecompressedSoFar[b]
            }
        } catch (dfe: DataFormatException) {
            dfe.printStackTrace()
        }
        inflater.end()
        return returnValues
    }

    /**
     * zlib compress 2 byte
     *
     * @param bytesToCompress
     * @return
     */
    private fun compressForZlib(bytesToCompress: ByteArray?): ByteArray {
        val deflater = Deflater()
        deflater.setInput(bytesToCompress)
        deflater.finish()
        val bytesCompressed =
            ByteArray(Short.MAX_VALUE.toInt())
        val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
        val returnValues = ByteArray(numberOfBytesAfterCompression)
        System.arraycopy(
            bytesCompressed,
            0,
            returnValues,
            0,
            numberOfBytesAfterCompression
        )
        return returnValues
    }

    /**
     * zlib compress 2 byte
     *
     * @param stringToCompress
     * @return
     */
    fun compressForZlib(stringToCompress: String): ByteArray? {
        var returnValues: ByteArray? = null
        try {
            returnValues = compressForZlib(
                stringToCompress.toByteArray(charset("UTF-8"))
            )
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        }
        return returnValues
    }

    /**
     * gzip compress 2 byte
     *
     * @param string
     * @return
     * @throws IOException
     */
    fun compressForGzip(string: String): ByteArray? {
        var os: ByteArrayOutputStream? = null
        var gos: GZIPOutputStream? = null
        try {
            os = ByteArrayOutputStream(string.length)
            gos = GZIPOutputStream(os)
            gos.write(string.toByteArray(charset("UTF-8")))
            return os.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeQuietly(gos)
            closeQuietly(os)
        }
        return null
    }

    /**
     * gzip decompress 2 string
     *
     * @param compressed
     * @return
     * @throws IOException
     */
    fun decompressForGzip(
        compressed: ByteArray,
        charsetName: String? = "UTF-8"
    ): String? {
        val bufferSize = compressed.size
        var gis: GZIPInputStream? = null
        var bais: ByteArrayInputStream? = null
        try {
            bais = ByteArrayInputStream(compressed)
            gis = GZIPInputStream(bais, bufferSize)
            val string = StringBuilder()
            val data = ByteArray(bufferSize)
            var bytesRead: Int
            while (gis.read(data).also { bytesRead = it } != -1) {
                string.append(String(data, 0, bytesRead, Charset.forName(charsetName)))
            }
            return string.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeQuietly(gis)
            closeQuietly(bais)
        }
        return null
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

    /**
     * 读取comment区域数据
     *
     * @return
     */
    fun readComment(): String? {
        // 获取文件路径
        val file = File(Utils.getApp().packageCodePath)
        var bytes: ByteArray?
        var accessFile: RandomAccessFile? = null
        try {
            accessFile = RandomAccessFile(file, "r")
            var index = accessFile.length()
            bytes = ByteArray(2)
            // 获取comment文件的位置
            index -= bytes.size
            accessFile.seek(index)
            // 获取comment中写入数据的大小byte类型
            accessFile.readFully(bytes)
            // 将byte转换成大小
            val contentLength: Int = stream2Short(bytes, 0).toInt()
            // 创建byte[]数据大小来存储写入的数据
            bytes = ByteArray(contentLength)
            index -= bytes.size
            accessFile.seek(index)
            // 读取数据
            accessFile.readFully(bytes)
            return String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (accessFile != null) {
                closeQuietly(accessFile)
            }
        }
        return null
    }

    /**
     * 字节数组转换成short
     *
     * @param stream
     * @param offset
     * @return
     */
    private fun stream2Short(stream: ByteArray, offset: Int): Short {
        val buffer = ByteBuffer.allocate(2)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(stream[offset])
        buffer.put(stream[offset + 1])
        return buffer.getShort(0)
    }
}