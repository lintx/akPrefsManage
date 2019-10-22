package org.lintx.akprefsmanage.model

import android.content.Context
import com.beust.klaxon.Klaxon
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.extensions.Item
import org.lintx.akprefsmanage.utils.PrefsManage
import org.lintx.akprefsmanage.R
import org.lintx.akprefsmanage.utils.OneDrive
import java.io.File
import java.io.InputStream
import java.lang.Exception

class DataModel(
    val fsList:ArrayList<PrefsModel> = ArrayList(),
    val tokenList:ArrayList<TokensModel> = ArrayList()
)

class PrefsModel(
    var name:String = "",
    val path:String = ""
)

class TokensModel(
    var name:String = "",
    val token:String = ""
)

class Config(
    var oneDriveSync:Boolean = false
)

class Prefs private constructor() {
    private var data:DataModel = DataModel()
    var fsList:ArrayList<PrefsModel> = ArrayList()
    var tokenList:ArrayList<TokensModel> = ArrayList()
    var config:Config = Config()

    companion object {
        private var instance: Prefs? = null
            get() {
                if (field == null) {
                    field = Prefs()
                }
                return field
            }
        fun get(): Prefs{
            return instance!!
        }
    }

    fun loadData(context: Context){
        loadPrefs(context)
        loadTokens(context)
        loadConfig(context)
        if (fsList.isEmpty() && tokenList.isEmpty()){
            tryLoadOldData(context)
        }
        if (fsList.isEmpty()){
            val f = context.getString(R.string.default_prefs_file)
            val manage = PrefsManage(context, f)
            if (manage.checkFile()){
                fsList.add(PrefsModel("默认存档",f))
                savePrefs(context)
            }
        }
    }

    private fun loadPrefs(context: Context){
        val file = getPrefsFile(context)
        try {
            val list: List<PrefsModel> = Klaxon().parseArray(file)!!
            for (item in list){
                fsList.add(item)
            }
        }catch (e:Exception){

        }
    }

    private fun loadConfig(context: Context){
        val file = getConfigFile(context)
        try {
            config = Klaxon().parse<Config>(file)!!
        }catch (e:Exception){

        }
    }

    private fun loadTokens(context: Context){
        val file = getTokensFile(context)
        try {
            val list: List<TokensModel> = Klaxon().parseArray(file)!!
            for (item in list){
                tokenList.add(item)
            }
        }catch (e:Exception){
        }
    }

    fun syncOneDriveTokens(context: Context,stream:InputStream){
        var changed = false
        try {
            val list: List<TokensModel> = Klaxon().parseArray(stream)!!
            for (token in list){
                println("onedrive file" + token.name + ":" + token.token)
                var localHas = false
                for (item in tokenList){
                    if (item.token==token.token){
                        localHas = true
                        break
                    }
                }
                if (!localHas){
                    changed = true
                    tokenList.add(token)
                }
            }
        }catch (e:Exception){

        }
        if (changed){
            saveTokens(context)
        }
    }

    private fun tryLoadOldData(context: Context){
        val path = context.getDir("data",Context.MODE_PRIVATE)
        if (!path.exists()){
            path.mkdirs()
        }
        val file = File(path,"data.json")
        if (!file.exists()){
            return
        }
        try {
            data = Klaxon().parse<DataModel>(file)!!
        }catch (e:Exception){

        }
        if (data.fsList.isNotEmpty() || data.tokenList.isNotEmpty()){
            fsList = data.fsList
            tokenList = data.tokenList
            savePrefs(context)
            saveTokens(context)
        }
        file.delete()
    }

    fun savePrefs(context: Context){
        val file = getPrefsFile(context)
        val json = Klaxon().toJsonString(fsList)
        file.writeText(json)
    }

    fun saveTokens(context: Context){
        val file = getTokensFile(context)
        val json = Klaxon().toJsonString(tokenList)
        file.writeText(json)

        if (config.oneDriveSync){
            val callback = object : ICallback<Item> {
                override fun success(result: Item?) {

                }

                override fun failure(ex: ClientException?) {

                }
            }
            OneDrive.get().updateTokens(context,callback)
        }
    }

    fun saveConfig(context: Context){
        val file = getConfigFile(context)
        val json = Klaxon().toJsonString(config)
        file.writeText(json)
    }

    private fun getPrefsFile(context: Context):File{
        val path = context.getDir("data",Context.MODE_PRIVATE)
        if (!path.exists()){
            path.mkdirs()
        }
        val file = File(path,"prefs.json")
        if (!file.exists()){
            file.createNewFile()
        }
        return file
    }

    fun getTokensFile(context: Context):File{
        val path = context.getDir("data",Context.MODE_PRIVATE)
        if (!path.exists()){
            path.mkdirs()
        }
        val file = File(path,"tokens.json")
        if (!file.exists()){
            file.createNewFile()
        }
        return file
    }

    private fun getConfigFile(context: Context):File{
        val path = context.getDir("data",Context.MODE_PRIVATE)
        if (!path.exists()){
            path.mkdirs()
        }
        val file = File(path,"config.json")
        if (!file.exists()){
            file.createNewFile()
        }
        return file
    }
}