package com.demo.bamboo.tba

import android.webkit.WebSettings
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.demo.bamboo.bambooApp
import com.demo.bamboo.bean.AdBean
import com.demo.bamboo.util.*
import com.github.shadowsocks.Core
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.ResponseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

object UploadTba {

    fun uploadTba(){
        HttpUtil.requestIp {
            getInstallReferrerClient()
            uploadSession()
        }
    }

    private fun uploadSession(){
        GlobalScope.launch {
            HttpUtil.uploadEvent(getCommonJson().apply { put("hokan",JSONObject()) })
        }
    }

    private fun getInstallReferrerClient(){
        if (!uploadHasReferrerTag() || !uploadNoReferrerTag()){
            val referrerClient = InstallReferrerClient.newBuilder(Core.app).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val response = referrerClient.installReferrer
                                uploadInstallEvent(response)
                            }
                            else->{
                                uploadInstallEvent(null)
                            }
                        }
                    }
                    runCatching {
                        referrerClient.endConnection()
                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }
    }

    private fun uploadInstallEvent(response: ReferrerDetails?) {
        if (null==response&& uploadNoReferrerTag()){
            return
        }
        if (null!=response&& uploadHasReferrerTag()){
            return
        }
        GlobalScope.launch {
            val commonJson = getCommonJson()
            commonJson.put("intrepid",JSONObject().apply {
                put("suitor",TbaInfo.getBuild())
                put("borne", WebSettings.getDefaultUserAgent(bambooApp))
                put("biotite","solitude")
                put("red",TbaInfo.getFirstInstallTime(bambooApp))
                put("brickbat",TbaInfo.getLastUpdateTime(bambooApp))
                if (null==response){
                    put("eventual","")
                    put("quatrain","")
                    put("upraise", 0)
                    put("wiggle", 0)
                    put("cull", 0)
                    put("sexy", 0)
                    put("rawhide", false)
                }else{
                    put("eventual",response.installReferrer)
                    put("quatrain",response.installVersion)
                    put("upraise", response.referrerClickTimestampSeconds)
                    put("wiggle", response.installBeginTimestampSeconds)
                    put("cull", response.referrerClickTimestampServerSeconds)
                    put("sexy", response.installBeginTimestampServerSeconds)
                    put("rawhide", response.googlePlayInstantParam)
                }
            })
            HttpUtil.uploadEvent(commonJson,install = true)
        }
    }

    fun uploadAdEventJson(
        type: String,
        value: AdValue,
        responseInfo: ResponseInfo?,
        adBean: AdBean,
        loadIp:String,
        showIp:String,
        loadCity:String,
        showCity:String
    ){
        GlobalScope.launch {
            val makeTbaCommonJson = getCommonJson()
            withContext(Dispatchers.Main){
                HttpUtil.uploadEvent(makeTbaCommonJson.apply {
                    put("newscast",value.valueMicros)
                    put("backwood",value.currencyCode)
                    put("lappet",getAdNetWork(responseInfo?.mediationAdapterClassName?:""))
                    put("snow","admob")
                    put("miser",adBean.bambooId)
                    put("conjugal",type)
                    put("escort","")
                    put("crowfoot",adBean.bambooType)
                    put("oldy",getPrecisionType(value.precisionType))
                    put("sedan", MobileAds.getVersion().toString())
                    put("wafer",loadIp)
                    put("andorra",showIp)
                    put("system","influx")
                    put("isopleth&r_city",loadCity)
                    put("isopleth&s_city",showCity)
                })
            }
        }
    }


    private fun getCommonJson():JSONObject{
        val jsonObject = JSONObject()
        jsonObject.put("arhat",JSONObject().apply {
            put("pestle",TbaInfo.getLogId())
            put("gershwin",TbaInfo.getBrand())
            put("enoch",TbaInfo.getOsVersion())
            put("manage",TbaInfo.getBundleId(bambooApp))
            put("crewmen",TbaInfo.getManufacturer())
            put("upgrade",TbaInfo.getDistinctId(bambooApp))
        })
        jsonObject.put("coulter",JSONObject().apply {
            put("incite",TbaInfo.getAndroidId(bambooApp))
            put("yost",TbaInfo.getNetworkType(bambooApp))
            put("torah",TbaInfo.getSystemLanguage())
            put("hairdo","weston")
            put("embolden",TbaInfo.getAppVersion(bambooApp))
            put("lumbago",HttpUtil.ip)
            put("befallen",TbaInfo.getZoneOffset())
            put("comply",TbaInfo.getGaid(bambooApp))
        })
        jsonObject.put("hand",JSONObject().apply {
            put("lewd",TbaInfo.getOperator(bambooApp))
            put("rode",System.currentTimeMillis())
            put("sunspot",TbaInfo.getScreenRes(bambooApp))
            put("slant",TbaInfo.getOsCountry())
            put("alderman",TbaInfo.getDeviceModel())
        })
        return jsonObject
    }
}