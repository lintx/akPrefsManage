package org.lintx.akprefsmanage

import android.content.Intent
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
import org.lintx.akprefsmanage.adapter.PrefsAdapter
import org.lintx.akprefsmanage.model.Prefs
import org.lintx.akprefsmanage.model.PrefsModel
import org.lintx.akprefsmanage.model.TokensModel
import org.lintx.akprefsmanage.utils.PrefsManage


class MainActivity : AppCompatActivity(),AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener {
    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        showPopup(view!!,position)
        return true
    }

    private fun showPopup(view: View,position: Int) {
        var popup: PopupMenu?
        popup = PopupMenu(this, view)
        popup.inflate(R.menu.prefs_action_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when(item!!.itemId){
                R.id.action_edit -> {
                    val titleView = EditText(this)
                    titleView.setText(prefs.data.fsList[position].name)
                    val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                    dialog.setTitle("修改存档别名")
                    dialog.setView(titleView)
                    dialog.setPositiveButton("确定") { _, _ ->
                        val name = titleView.text.toString()
                        if (name==""){
                            Toast.makeText(this,"存档别名不能为空",Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        prefs.data.fsList[position].name = name
                        prefs.saveData(this)
                        adapter!!.notifyDataSetChanged()
                    }
                    dialog.setNegativeButton("取消",null)
                    dialog.show()
                }
                R.id.action_delete -> {
                    val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                    dialog.setTitle("操作确认")
                    dialog.setMessage("你确定要从本APP中将这个存档移除吗？\n从本APP中移除存档不会影响明日方舟存档。")
                    dialog.setPositiveButton("确定") { _, _ ->
                        prefs.data.fsList.removeAt(position)
                        prefs.saveData(this)
                        adapter!!.notifyDataSetChanged()
                    }
                    dialog.setNegativeButton("取消",null)
                    dialog.show()
                }
            }
            true
        })

        popup.show()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startActivity(Intent(this,TokenActivity::class.java).apply { putExtra("prefs",prefs.data.fsList[position].path) })
    }

    private var adapter :PrefsAdapter?=null
    private val prefs = Prefs.get()
    private var add_name = ""
    private var add_path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs.loadData(this)

        adapter = PrefsAdapter(prefs.data.fsList,this)
        listView.adapter = adapter
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add->{
                val layout = View.inflate(this,R.layout.add_fs_dialog_layout,null)
                val titleView :EditText = layout.findViewById(R.id.title) as EditText
                val fileView :EditText = layout.findViewById(R.id.filename) as EditText
                titleView.setText(add_name)
                fileView.setText(add_path)
                val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                dialog.setTitle("添加本地存档")
                dialog.setView(layout)
                dialog.setPositiveButton("确定") { _, _ ->
                    add_name = titleView.text.toString()
                    add_path = fileView.text.toString()
                    if (add_name==""){
                        Toast.makeText(this,"存档别名不能为空",Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val manage = PrefsManage(this, add_path)
                    if (!manage.checkFile()){
                        return@setPositiveButton
                    }

                    var isManage = false
                    for (item in prefs.data.fsList){
                        if (item.path==add_path){
                            isManage = true
                            break
                        }
                    }
                    if (isManage){
                        Toast.makeText(this,"该存档文件已经添加过",Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    prefs.data.fsList.add(PrefsModel(add_name,add_path))
                    prefs.saveData(this)
                    add_name = ""
                    add_path = ""
                    adapter!!.notifyDataSetChanged()
                }
                dialog.setNegativeButton("取消",null)
                dialog.show()
            }
            R.id.action_add_token -> {
                val layout = View.inflate(this,R.layout.add_token_dialog_layout,null)
                val titleView :EditText = layout.findViewById(R.id.title) as EditText
                val tokenView :EditText = layout.findViewById(R.id.token) as EditText
                val dialog = AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                dialog.setTitle("手动添加游客帐号")
                dialog.setView(layout)
                dialog.setPositiveButton("确定") { _, _ ->
                    val name = titleView.text.toString()
                    val token = tokenView.text.toString()
                    if (name==""){
                        Toast.makeText(this,"游客帐号别名不能为空",Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    var isManage = false
                    for (item in prefs.data.tokenList){
                        if (item.token==token){
                            isManage = true
                            break
                        }
                    }
                    if (isManage){
                        Toast.makeText(this,"该游客帐号已经添加过",Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    prefs.data.tokenList.add(TokensModel(name,token))
                    prefs.saveData(this)
                }
                dialog.setNegativeButton("取消",null)
                dialog.show()
            }
        }
        return true
    }
}
