package com.ibroadlink.library.base.utils

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.StringWriter
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class BLDXmlUtils {
    var docFactory: DocumentBuilderFactory? = null
    var docBuilder: DocumentBuilder? = null
    var doc: Document? = null

    fun parse(xml: String?): Boolean {
        if (xml == null || xml.length < 16) return false
        val inStream = ByteArrayInputStream(xml.toByteArray())
        return this.parse(inStream)
    }

    fun parse(inStream: InputStream?): Boolean {
        try {
            doc = docBuilder!!.parse(inStream)
            return true
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun findFirstElement(parent: Node?, tag: String): Node? {
        if (parent == null || !parent.hasChildNodes()) return null
        var n = parent.firstChild
        while (n != null) {
            if (n.nodeName == tag) return n
            n = n.nextSibling
        }
        return null
    }

    fun getInt(tags: String?, value: Int = 0): Int {
        val s = this.getText(tags)
        return if (s != null && s.length < 11) s.toInt() else value
    }

    fun getBool(tags: String?, value: Boolean = false): Boolean {
        val s = this.getText(tags)
        return s?.toBoolean() ?: value
    }

    fun getText(tags: String?, value: String? = null): String? {
        return getText(tags) ?: return value
    }

    fun getText(tags: String?): String? {
        var n: Node? = doc!!.firstChild ?: return null
        val tk = StringTokenizer(tags, "/")
        val s = tk.nextToken()
        if (n?.nodeName != s) return null
        while (n != null && tk.hasMoreTokens()) {
            n = findFirstElement(n, tk.nextToken())
        }
        return if (n != null && n.hasChildNodes()) n.firstChild.nodeValue else null
    }

    fun setInt(tags: String?, value: Int) {
        setText(tags, value.toString())
    }

    fun setText(tags: String?, value: String?) {
        var n = doc!!.firstChild
        var n2: Node?
        var s: String
        val tk = StringTokenizer(tags, "/")
        s = tk.nextToken()
        if (n == null) {
            n = doc!!.createElement(s)
            doc!!.appendChild(n)
        }
        while (tk.hasMoreTokens()) {
            s = tk.nextToken()
            n2 = findFirstElement(n, s)
            if (n2 == null) {
                n2 = doc!!.createElement(s)
                n!!.appendChild(n2)
            }
            n = n2
        }
        if (value != null) n!!.appendChild(doc!!.createTextNode(value))
    }

    override fun toString(): String {
        val factory = TransformerFactory.newInstance()
        val former: Transformer
        try {
            former = factory.newTransformer()
            val writer = StringWriter()
            val result = StreamResult(writer)
            val source = DOMSource(doc)
            former.transform(source, result)
            return writer.toString()
        } catch (e1: TransformerException) {
            e1.printStackTrace()
        }
        return ""
    }

    init {
        docFactory = DocumentBuilderFactory.newInstance()
        try {
            docBuilder = docFactory!!.newDocumentBuilder()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        doc = docBuilder!!.newDocument()
    }
}