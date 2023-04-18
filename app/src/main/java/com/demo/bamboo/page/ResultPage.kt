package com.demo.bamboo.page

import com.demo.bamboo.R
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.interfaces.ServerTimeInterface
import com.demo.bamboo.server.ServerTime
import kotlinx.android.synthetic.main.activity_result.*

class ResultPage:BasePage(), ServerTimeInterface {
    private var connect=false

    override fun layout(): Int = R.layout.activity_result

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        connect=intent.getBooleanExtra("connect",false)
        if (connect){
            tv_title.text="Connect success"
            iv_center.setImageResource(R.drawable.connected)
            tv_status.text="Connected Successfully"
            ServerTime.setInterface(this)
        }else{
            tv_title.text="Disconnected"
            iv_center.setImageResource(R.drawable.connecting)
            tv_status.text="VPN Disconnected"
            tv_time.text=ServerTime.getTotalTime()
        }
    }

    override fun connectTime(time: String) {
        tv_time.text=time
    }

    override fun onDestroy() {
        super.onDestroy()
        if(connect){
            ServerTime.setInterface(this)
        }
    }
}