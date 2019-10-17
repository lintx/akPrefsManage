package org.lintx.akprefsmanage.utils

import android.content.Context
import android.widget.Toast
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class PrefsManage(private val context: Context, path:String) {
    private val fp = FilePermission(path)
    private val file = File(path)
    private val builderFactory = DocumentBuilderFactory.newInstance()
    private val documentBuilder = builderFactory.newDocumentBuilder()
    private val attrName = "name"
    private val attrValue = "LOGIN_SDK_TOKEN_GUEST"

    fun getToken():String{
        if (!this.checkFile()){
            return "null"
        }
        if (!fp.setRead()){
            Toast.makeText(this.context,"设置存档文件权限失败", Toast.LENGTH_SHORT).show()
            return "null"
        }
        val token = readToken()
        fp.cancel()
        return token
    }

    fun writeToken(token:String){
        fp.setReadWrite()
        if (!fp.setReadWrite()){
            Toast.makeText(context,"设置存档文件权限失败", Toast.LENGTH_SHORT).show()
            return
        }
        val old = readToken()
        if (old==token){
            return
        }
        val parse = documentBuilder.parse(file)
        var mapNodes = parse.getElementsByTagName("map")
        if (mapNodes.length<1){
            parse.createElement("map")
            mapNodes = parse.getElementsByTagName("map")
        }

        val mapNode = mapNodes.item(0)
        if (old == "null"){
            val node = parse.createElement("string")
            node.setAttribute(attrName,attrValue)
            node.textContent = token
            mapNode.appendChild(node)
        }else{
            val nodes = mapNode.childNodes
            (0 until nodes.length)
                .filter { nodes.item(it).nodeType == Node.ELEMENT_NODE }
                .filter { nodes.item(it).nodeName == "string" }
                .forEach {
                    val node = nodes.item(it)
                    var name = ""
                    (0 until node.attributes.length)
                        .filter { ait -> node.attributes.item(ait).nodeName == "name" }
                        .forEach { ait -> name = node.attributes.item(ait).nodeValue }
                    if (name=="LOGIN_SDK_TOKEN_GUEST"){
                        node.textContent = token
                    }
                }
        }

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        //是否自动换行
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(DOMSource(parse), StreamResult(file))

        fp.cancel()
    }

    private fun readToken():String{
        val parse = documentBuilder.parse(file)
        val mapNodes = parse.getElementsByTagName("map")
        if (mapNodes.length==1){
            val nodes = mapNodes.item(0).childNodes
            (0 until nodes.length)
                .filter { nodes.item(it).nodeType == Node.ELEMENT_NODE }
                .filter { nodes.item(it).nodeName == "string" }
                .forEach {
                    val node = nodes.item(it)
                    var name = ""
                    (0 until node.attributes.length)
                        .filter { ait -> node.attributes.item(ait).nodeName == attrName }
                        .forEach { ait ->
                            name = node.attributes.item(ait).nodeValue
                        }
                    if (name==attrValue){
                        return node.textContent
                    }
                }
        }

        return "null"
    }

    fun checkFile():Boolean{
        if (!file.exists()){
            Toast.makeText(context,"存档文件不存在", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!file.isFile){
            Toast.makeText(context,"存档文件类型错误",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}