package com.demo.bamboo

import android.app.Application
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.page.HomePage
import com.demo.bamboo.server.ServerInfo
import com.demo.bamboo.util.AppUtil
import com.demo.bamboo.util.processName
import com.github.shadowsocks.Core
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

lateinit var bambooApp:BambooApp
class BambooApp:Application() {
    override fun onCreate() {
        super.onCreate()
        bambooApp=this
        Core.init(this,HomePage::class)
        if (!packageName.equals(processName(this))){
            return
        }
        Firebase.initialize(this)
        MMKV.initialize(this)
        AppUtil.register(this)
        Fire.readFire()
        ServerInfo.writeLocalServer()
    }
}