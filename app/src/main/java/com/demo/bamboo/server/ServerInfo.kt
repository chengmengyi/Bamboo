package com.demo.bamboo.server

import com.demo.bamboo.bean.ServerBean
import com.demo.bamboo.conf.Local

object ServerInfo {
    private val cityList= arrayListOf<String>()
    private val fireServerList= arrayListOf<ServerBean>()

    fun getAllServer()=fireServerList.ifEmpty { Local.localServerList }

    fun writeLocalServer(){
        Local.localServerList.forEach { it.writeServerId() }
    }

    fun getRandomServer():ServerBean{
        val serverList = getAllServer()
        if (!cityList.isNullOrEmpty()){
            val filter = serverList.filter { cityList.contains(it.bamboo_ci) }
            if (!filter.isNullOrEmpty()){
                return filter.random()
            }
        }
        return serverList.random()
    }
}