package com.demo.bamboo.util

import com.demo.bamboo.conf.Fire
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object LimitUtil {
    var isLimitUser=false

    private var maxShow=50
    private var maxClick=1

    private var click=0
    private var show=0

    private val refresh= hashMapOf<String,Boolean>()

    fun checkLimit(){
        HttpUtil.requestIp {
            isLimitUser=HttpUtil.countryCode.limitArea()
        }
    }

    fun resetRefresh(){
        refresh.clear()
    }

    fun canRefresh(type:String)=refresh[type]?:true

    fun setRefreshStatus(type:String,boolean: Boolean){
        refresh[type]=boolean
    }

    fun setNum(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            maxShow=jsonObject.optInt("pretty")
            maxClick=jsonObject.optInt("cat")
        }
    }

    fun readNum(){
        click= MMKV.defaultMMKV().decodeInt(key("maxClick"),0)
        show= MMKV.defaultMMKV().decodeInt(key("maxShow"),0)
    }

    fun updateClick(){
        click++
        MMKV.defaultMMKV().encode(key("maxClick"), click)
    }

    fun updateShow(){
        show++
        MMKV.defaultMMKV().encode(key("maxShow"), show)
    }

    fun hasLimit()= click>= maxClick|| show>= maxShow

    private fun key(string:String)="${string}...${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}"
}