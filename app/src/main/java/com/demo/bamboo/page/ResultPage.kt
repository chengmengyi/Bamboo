package com.demo.bamboo.page

import com.demo.bamboo.R
import com.demo.bamboo.admob.ShowNativeAd
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Local
import com.demo.bamboo.interfaces.ServerTimeInterface
import com.demo.bamboo.server.ServerTime
import com.demo.bamboo.util.LimitUtil
import kotlinx.android.synthetic.main.activity_result.*

class ResultPage:BasePage(), ServerTimeInterface {
    private var connect=false
    private val showBottomAd by lazy { ShowNativeAd(Local.RESULT,this) }

    override fun layout(): Int = R.layout.activity_result

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        connect=intent.getBooleanExtra("connect",false)
        tv_time.isSelected=connect
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

    override fun onResume() {
        super.onResume()
        showBottomAd.loopCheckNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(connect){
            ServerTime.setInterface(this)
        }
        showBottomAd.stopLoop()
        LimitUtil.setRefreshStatus(Local.RESULT,true)
    }
}