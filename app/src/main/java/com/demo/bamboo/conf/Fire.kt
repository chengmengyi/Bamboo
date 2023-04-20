package com.demo.bamboo.conf

import com.demo.bamboo.BuildConfig
import com.demo.bamboo.util.LimitUtil
import com.demo.bamboo.util.ReferUtil
import com.demo.bamboo.util.str2Int
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.util.*


object Fire {
    private var bamboo_start="1"
    private var bamboo_ratio="50"
    private var bamboo_ref="2"
    private var bamboo_k="1"

    var isPlanB=false
    var isBlack=true
    var coldLoad=true

    fun readFire(){
        if(BuildConfig.DEBUG){
            return
        }
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful){
                parseConfig(remoteConfig.getString("bamboo_config"))
                saveAd(remoteConfig.getString("bamboo_ad"))
            }
        }
    }

    private fun saveAd(string: String){
        runCatching {
            MMKV.defaultMMKV().encode("bamboo_ad",string)
            LimitUtil.setNum(string)
        }
    }

    private fun parseConfig(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            bamboo_start=jsonObject.optString("bamboo_start")
            bamboo_ratio=jsonObject.optString("bamboo_ratio")
            bamboo_ref=jsonObject.optString("bamboo_ref")
            bamboo_k=jsonObject.optString("bamboo_k")
        }
    }

    fun getAdStr():String{
        val ad = MMKV.defaultMMKV().decodeString("bamboo_ad") ?: ""
        if(ad.isEmpty()){
            return Local.localAd3
        }
        return ad
    }

    fun checkIsPlanB(){
        isPlanB=false
        if((coldLoad&&bamboo_start=="1")||bamboo_start=="2"){
            val nextInt = Random().nextInt(100)
            isPlanB = str2Int(bamboo_ratio)>=nextInt
        }
    }

    fun checkBlackLimitInterAd(type:String)= (type==Local.CONNECT||type==Local.BACK)&&isBlack&& bamboo_k=="1"

    fun checkFireConfigLimitInterAd()=when(bamboo_ref){
        "1"-> false
        "2"-> !ReferUtil.isBuyUser()
        "3"-> !ReferUtil.isFB()
        else-> true
    }
}