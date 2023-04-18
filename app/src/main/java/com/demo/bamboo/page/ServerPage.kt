package com.demo.bamboo.page

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.bamboo.R
import com.demo.bamboo.adapter.ServerAdapter
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.bean.ServerBean
import com.demo.bamboo.server.ServerUtil
import kotlinx.android.synthetic.main.activity_server.*

class ServerPage:BasePage() {
    override fun layout(): Int = R.layout.activity_server

    override fun initView() {
        immersionBar.statusBarView(top).init()

        server_list.apply {
            layoutManager=LinearLayoutManager(this@ServerPage)
            adapter=ServerAdapter(this@ServerPage){ click(it) }
        }

        iv_back.setOnClickListener { finish() }
    }

    private fun click(serverBean: ServerBean){
        val current = ServerUtil.currentServer
        val connected = ServerUtil.isConnected()
        if(connected&&current.ip!=serverBean.ip){
            AlertDialog.Builder(this).apply {
                setMessage("If you want to connect to another VPN, you need to disconnect the current connection first. Do you want to disconnect the current connection?")
                setPositiveButton("Sure", object :OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        chooseServer("dis",serverBean)
                    }

                })
                setNegativeButton("Cancel",null)
                show()
            }
        }else{
            if (connected){
                chooseServer("",serverBean)
            }else{
                chooseServer("con",serverBean)
            }
        }
    }

    private fun chooseServer(action:String,serverBean: ServerBean){
        ServerUtil.currentServer=serverBean
        setResult(417, Intent().apply {
            putExtra("action",action)
        })
        finish()
    }
}