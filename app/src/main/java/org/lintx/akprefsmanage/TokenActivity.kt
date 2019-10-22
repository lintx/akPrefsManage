package org.lintx.akprefsmanage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_main.*
import org.lintx.akprefsmanage.adapter.TokensAdapter
import org.lintx.akprefsmanage.model.Prefs
import org.lintx.akprefsmanage.model.TokensModel
import org.lintx.akprefsmanage.utils.PrefsManage

class TokenActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var prefsPath :String = ""
    private var prefsName :String = ""
    private var token = "null"
    private val prefs = Prefs.get()
    private var adapter :TokensAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)

        val actionBar = supportActionBar

        prefsPath = intent.extras!!.getString("prefsPath").toString()
        prefsName = intent.extras!!.getString("prefsName").toString()
        actionBar!!.title = "存档管理:" + prefsName
        actionBar.setDisplayHomeAsUpEnabled(true)

        adapter = TokensAdapter(prefs.tokenList,this)
        listView.adapter = adapter
        listView.onItemClickListener = this

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        showPopup(view!!,position)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.token_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_load_token -> {
                if (token=="null"){
                    val manage = PrefsManage(this,prefsPath)
                    if (!manage.checkFile()){
                        return true
                    }
                    token = manage.getToken()
                    if (token=="null"){
                        Toast.makeText(this,"获取游客帐号数据失败",Toast.LENGTH_SHORT).show()
                        return true
                    }

                    prefs.tokenList.forEach {
                        if (it.token==token){
                            Toast.makeText(this,"该游客帐号已经添加过",Toast.LENGTH_SHORT).show()
                            return true
                        }
                    }
                }

                val titleView = EditText(this)
                val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                dialog.setTitle("设置游客帐号别名")
                dialog.setView(titleView)
                dialog.setPositiveButton("确定") { _, _ ->
                    val name = titleView.text.toString()
                    if (name==""){
                        Toast.makeText(this,"游客帐号别名不能为空",Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    prefs.tokenList.add(TokensModel(name,token))
                    prefs.saveTokens(this)
                    token = "null"
                    adapter!!.notifyDataSetChanged()
                }
                dialog.setNegativeButton("取消",null)
                dialog.show()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPopup(view: View,position: Int) {
        val popup: PopupMenu?
        popup = PopupMenu(this, view)
        popup.inflate(R.menu.token_action_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when(item!!.itemId){
                R.id.action_edit -> {
                    val titleView = EditText(this)
                    titleView.setText(prefs.tokenList[position].name)
                    val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                    dialog.setTitle("修改游客帐号别名")
                    dialog.setView(titleView)
                    dialog.setPositiveButton("确定") { _, _ ->
                        val name = titleView.text.toString()
                        if (name==""){
                            Toast.makeText(this,"游客帐号别名不能为空",Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        prefs.tokenList[position].name = name
                        prefs.saveTokens(this)
                        adapter!!.notifyDataSetChanged()
                    }
                    dialog.setNegativeButton("取消",null)
                    dialog.show()
                }
                R.id.action_delete -> {
                    val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                    dialog.setTitle("操作确认")
                    dialog.setMessage("你确定要从本APP中将这个游客帐号移除吗？\n从本APP中移除存档不会影响明日方舟存档，\n但是你可能永远也找不回这个游客帐号了。")
                    dialog.setPositiveButton("确定") { _, _ ->
                        prefs.tokenList.removeAt(position)
                        prefs.saveTokens(this)
                        adapter!!.notifyDataSetChanged()
                    }
                    dialog.setNegativeButton("取消",null)
                    dialog.show()
                }
                R.id.action_apply -> {
                    val manage = PrefsManage(this,prefsPath)
                    if (manage.checkFile()){
                        val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                        dialog.setTitle("操作确认")
                        dialog.setMessage("你确定要将这个游客帐号应用到明日方舟存档中吗？\n应用后如果明日方舟存档中有未保存的游客帐号，\n那么未保存的游客帐号将无法找回。")
                        dialog.setPositiveButton("确定") { _, _ ->
                            manage.writeToken(prefs.tokenList[position].token)
                            Toast.makeText(this,"操作成功",Toast.LENGTH_SHORT).show()
                        }
                        dialog.setNegativeButton("取消",null)
                        dialog.show()
                    }
                }
            }
            true
        })

        popup.show()
    }
}
