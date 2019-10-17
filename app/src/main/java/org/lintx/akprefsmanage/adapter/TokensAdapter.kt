package org.lintx.akprefsmanage.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.lintx.akprefsmanage.R
import org.lintx.akprefsmanage.model.TokensModel

class TokensAdapter (private val list:ArrayList<TokensModel>, private val context:Context): BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: TokensViewHolder
        val v: View
        if (convertView == null){
            v = View.inflate(context,R.layout.list_item_token,null)
            holder = TokensViewHolder(v)
            v.tag = holder
        }else{
            v = convertView
            holder = v.tag as TokensViewHolder
        }
        holder.titleView.text = list[position].name
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

class TokensViewHolder(viewItem: View){
    var titleView: TextView = viewItem.findViewById(R.id.title) as TextView
}