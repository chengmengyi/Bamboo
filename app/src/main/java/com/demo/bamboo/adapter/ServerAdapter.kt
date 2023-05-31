package com.demo.bamboo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.bamboo.R
import com.demo.bamboo.bean.ServerBean
import com.demo.bamboo.server.ServerInfo
import com.demo.bamboo.server.ServerUtil
import com.demo.bamboo.util.getServerLogo
import kotlinx.android.synthetic.main.item_server.view.*

class ServerAdapter(
    private val context: Context,
    private val list:ArrayList<ServerBean>,
    private val click:(serverBean:ServerBean)->Unit
):Adapter<ServerAdapter.ServerView>() {
//    private val list= arrayListOf<ServerBean>()
//    init {
//        list.add(ServerBean())
//        list.addAll(ServerInfo.getAllServer())
//    }
//
    inner class ServerView(view:View):ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerView {
        return ServerView(LayoutInflater.from(context).inflate(R.layout.item_server,parent,false))
    }

    override fun getItemCount(): Int =list.size
    
    override fun onBindViewHolder(holder: ServerView, position: Int) {
        with(holder.itemView){
            item_layout.isSelected=position%2==0
            val serverBean = list[position]
            tv_name.text=if(serverBean.isSuperFast()){
                serverBean.bamboo_ry
            }else{
                "${serverBean.bamboo_ry} - ${serverBean.bamboo_ci}"
            }
            iv_logo.setImageResource(getServerLogo(serverBean.bamboo_ry))
            iv_sel.isSelected=serverBean.bamboo_ip==ServerUtil.currentServer.bamboo_ip
        }
    }
}