package org.lintx.akprefsmanage.model

import android.content.Context
import com.beust.klaxon.Klaxon
import org.lintx.akprefsmanage.utils.PrefsManage
import org.lintx.akprefsmanage.R
import java.io.File
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

class Prefs private constructor() {
    var data:DataModel = DataModel()

    companion object {

        private var instance: Prefs? = null
            get() {
                if (field == null) {
                    field = Prefs()
                }
                return field
            }
        fun get(): Prefs{
            //细心的小伙伴肯定发现了，这里不用getInstance作为为方法名，是因为在伴生对象声明时，内部已有getInstance方法，所以只能取其他名字
            return instance!!
        }
    }

    fun loadData(context: Context){
        val file = getStorgeFile(context)
        try {
            data = Klaxon().parse<DataModel>(file)!!
        }catch (e:Exception){
            
        }
        if (data.fsList.isEmpty()){
            val f = context.getString(R.string.default_prefs_file)
            val manage = PrefsManage(context, f)
            if (manage.checkFile()){
                data.fsList.add(PrefsModel("默认存档",f))
                saveData(context)
            }
        }
    }

    fun saveData(context: Context){
        val file = getStorgeFile(context)
        val json = Klaxon().toJsonString(data)
        file.writeText(json)
    }

    private fun getStorgeFile(context: Context):File{
        val path = context.getDir("data",Context.MODE_PRIVATE)
        if (!path.exists()){
            path.mkdirs()
        }
        val file = File(path,"data.json")
        if (!file.exists()){
            file.createNewFile()
        }
        return file
    }
}