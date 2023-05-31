package com.demo.bamboo.server

import android.os.Bundle
import com.demo.bamboo.admob.LoadAdUtil
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.bean.ServerBean
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.interfaces.ServerStatusInterface
import com.demo.bamboo.tba.SetPointUtil
import com.demo.bamboo.util.HttpUtil
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ServerUtil: ShadowsocksConnection.Callback {
    private var connecting=false
    private var autoConnect=false
    private var basePage:BasePage?=null
    var state = BaseService.State.Stopped
    var currentServer= ServerBean()
    var lastServer= ServerBean()
    var fastServer= ServerBean()
    private val sc= ShadowsocksConnection(true)
    private var serverStatusInterface:ServerStatusInterface?=null

    fun init(basePage: BasePage,serverStatusInterface:ServerStatusInterface){
        this.basePage=basePage
        this.serverStatusInterface=serverStatusInterface
        sc.connect(basePage,this)
    }

    fun connect(autoConnect: Boolean){
        this.autoConnect=autoConnect
        state= BaseService.State.Connecting
        GlobalScope.launch {
            if (currentServer.isSuperFast()){
                val fast = ServerInfo.getFastServer()
                if(null!=fast){
                    fastServer=fast
                    DataStore.profileId = fastServer.getServerId()
                }
            }else{
                DataStore.profileId = currentServer.getServerId()
            }
            Core.startService()
        }
    }
    fun disconnect(){
        state= BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun isConnected()= state== BaseService.State.Connected

    fun isDisconnected()= state== BaseService.State.Stopped

    fun isConnectingOrStopping()= state== BaseService.State.Connecting||state== BaseService.State.Stopping

    fun connectServerSuccess(connect: Boolean)=if (connect) isConnected() else isDisconnected()

    fun getConnectedCurrentIp()=if (currentServer.isSuperFast()) fastServer.bamboo_ip else currentServer.bamboo_ip


    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (isConnected()){
            lastServer= currentServer
            ServerTime.start()

            HttpUtil.checkHeartBeat(true)
        }
        if (isDisconnected()){
            ServerTime.end()
            val bundle = Bundle()
            bundle.putLong("time",ServerTime.getTimeInt())
            SetPointUtil.point("bamboo_v_time",bundle=bundle)
            HttpUtil.checkHeartBeat(false)
            serverStatusInterface?.disconnectSuccess()
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.state=state
        if (isConnected()){
            lastServer= currentServer
            serverStatusInterface?.connectSuccess()
        }
    }

    override fun onBinderDied() {
        basePage?.let {
            sc.disconnect(it)
        }
    }

    fun onDestroy(){
        onBinderDied()
        basePage=null
        serverStatusInterface=null
    }
}