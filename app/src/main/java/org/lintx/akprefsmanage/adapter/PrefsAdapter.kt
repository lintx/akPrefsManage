package org.lintx.akprefsmanage.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.lintx.akprefsmanage.R
import org.lintx.akprefsmanage.model.PrefsModel

class PrefsAdapter (private val list:ArrayList<PrefsModel>, private val context:Context): BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: PrefsViewHolder
        val v: View
        if (convertView == null){
            v = View.inflate(context,R.layout.list_item,null)
            holder = PrefsViewHolder(v)
            v.tag = holder
        }else{
            v = convertView
            holder = v.tag as PrefsViewHolder
        }
        holder.titleView.text = list[position].name
        holder.descView.text = list[position].path
//        holder
        return v
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}

class PrefsViewHolder(viewItem: View){
    var titleView: TextView = viewItem.findViewById(R.id.title) as TextView
    var descView: TextView = viewItem.findViewById(R.id.desc) as TextView
}