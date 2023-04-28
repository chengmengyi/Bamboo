package com.demo.bamboo.server

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.demo.bamboo.bean.ServerBean
import com.demo.bamboo.conf.Local
import com.demo.bamboo.util.HttpUtil
import com.demo.bamboo.dialog.LoadingDialog
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

object ServerInfo {
    var loadingServer=false
    private var fastServerList= arrayListOf<ServerBean>()
    private var allServerList= arrayListOf<ServerBean>()


    fun writeLocalServer(){
        Local.localServerList.forEach { it.writeServerId() }
    }

    fun getFastServer():ServerBean?{
        if(fastServerList.isEmpty()){
            return null
        }
        return fastServerList.random(Random(System.currentTimeMillis()))
    }

    fun getAllServerList():ArrayList<ServerBean>{
        val list= arrayListOf<ServerBean>()
        if(allServerList.isNotEmpty()){
            list.add(ServerBean())
            list.addAll(allServerList)
        }
        return list
    }

    fun checkHasFast(manager: FragmentManager, hasFast:(has:Boolean)->Unit){
        if(fastServerList.isEmpty()){
            if(!loadingServer){
                HttpUtil.getServerList()
            }
            LoadingDialog{
                hasFast.invoke(false)
            }.show(manager,"LoadingDialog")
        }else{
            hasFast.invoke(true)
        }
    }

    fun checkToServerPage(manager: FragmentManager, toServerPage:()->Unit){
        if(allServerList.isEmpty()){
            if(loadingServer){
                LoadingDialog{}.show(manager,"LoadingDialog")
            }else{
                HttpUtil.getServerList()
                LoadingDialog{
                    toServerPage.invoke()
                }.show(manager,"LoadingDialog")
            }
        }else{
            toServerPage.invoke()
        }
    }

    fun parseServerJson(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            if(jsonObject.optInt("code")==200){
                val data = jsonObject.getJSONObject("data")
                fastServerList.clear()
                allServerList.clear()
                parseServerInfo(data.getJSONArray("QELnqi"), fastServerList)
                parseServerInfo(data.getJSONArray("Nvtv"), allServerList)
            }
        }
    }

    private fun parseServerInfo(jsonArray: JSONArray, list:ArrayList<ServerBean>){
        runCatching {
            for (index in 0 until jsonArray.length()){
                val json = jsonArray.getJSONObject(index)
                val serverBean = ServerBean(
                    bamboo_pd = json.optString("UTyRExcnc"),
                    bamboo_por = json.optInt("eHl"),
                    bamboo_ip = json.optString("UPxqcyr"),
                    bamboo_enc = json.optString("QOGb"),
                    bamboo_ry = json.optString("uNN"),
                    bamboo_ci = json.optString("oOUHLQQGo"),
                )
                serverBean.writeServerId()
                list.add(serverBean)
            }
        }
    }
}