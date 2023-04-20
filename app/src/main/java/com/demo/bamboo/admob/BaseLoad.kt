package com.demo.bamboo.admob

import com.demo.bamboo.bambooApp
import com.demo.bamboo.bean.AdBean
import com.demo.bamboo.bean.AdResultBean
import com.demo.bamboo.server.ServerUtil
import com.demo.bamboo.tba.UploadTba.uploadAdEventJson
import com.demo.bamboo.util.HttpUtil
import com.demo.bamboo.util.LimitUtil
import com.demo.bamboo.util.bambooLog
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

abstract class BaseLoad {
    private val loadAdIpMap= hashMapOf<String,String>()
    private val loadAdCityMap= hashMapOf<String,String?>()

    fun loadAdByType(type: String, adBean: AdBean, result: (bean: AdResultBean?) -> Unit){
        bambooLog("start load $type ad, ${adBean.toString()}")
        when(adBean.bambooType){
            "open"->loadOpen(type, adBean, result)
            "interstitial"->loadInt(type, adBean, result)
            "native"->loadNative(type, adBean, result)
        }
    }

    private fun loadOpen(type: String,adBean: AdBean, result: (bean: AdResultBean?) -> Unit){
        AppOpenAd.load(
            bambooApp,
            adBean.bambooId,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    bambooLog("load ad success----$type")
                    setLoadAdIpCityName(type)
                    p0.setOnPaidEventListener {
                        onAdEvent(type, it, p0.responseInfo, adBean)
                    }
                    result.invoke(AdResultBean(time = System.currentTimeMillis(), ad = p0))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    bambooLog("load ad fail----$type---${p0.message}")
                    result.invoke(null)
                }
            }
        )
    }
    private fun loadInt(type: String,adBean: AdBean, result: (bean: AdResultBean?) -> Unit){
        InterstitialAd.load(
            bambooApp,
            adBean.bambooId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    bambooLog("load ad fail----$type---${p0.message}")
                    result.invoke(null)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    bambooLog("load ad success----$type")
                    setLoadAdIpCityName(type)
                    p0.setOnPaidEventListener {
                        onAdEvent(type, it, p0.responseInfo, adBean)
                    }
                    result.invoke(AdResultBean(time = System.currentTimeMillis(), ad = p0))
                }
            }
        )
    }

    private fun loadNative(type: String,adBean: AdBean, result: (bean: AdResultBean?) -> Unit){
        AdLoader.Builder(
            bambooApp,
            adBean.bambooId,
        ).forNativeAd {p0->
            bambooLog("load ad success----$type")
            setLoadAdIpCityName(type)
            p0.setOnPaidEventListener {
                onAdEvent(type, it, p0.responseInfo, adBean)
            }
            result.invoke(AdResultBean(time = System.currentTimeMillis(), ad = p0))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    bambooLog("load ad fail----$type---${p0.message}")
                    result.invoke(null)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    LimitUtil.updateClick()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_TOP_LEFT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun onAdEvent(type: String, value: AdValue, responseInfo: ResponseInfo?, adBean: AdBean){
        uploadAdEventJson(type,value,responseInfo,adBean,loadAdIpMap[type]?:"",getCurrentIp(),loadAdCityMap[type]?:"null",getCurrentCityName())
    }

    private fun setLoadAdIpCityName(adType: String){
        loadAdIpMap[adType]=getCurrentIp()
        loadAdCityMap[adType]=getCurrentCityName()
    }

    private fun getCurrentCityName() = if(ServerUtil.isConnected()){
        if (ServerUtil.currentServer.isSuperFast()){
            ServerUtil.fastServer.bamboo_ci
        }else{
            ServerUtil.currentServer.bamboo_ci
        }

    }else{
        "null"
    }

    private fun getCurrentIp()=if(ServerUtil.isConnected()){
        if (ServerUtil.currentServer.isSuperFast()){
            ServerUtil.fastServer.bamboo_ip
        }else{
            ServerUtil.fastServer.bamboo_ip
        }
    }else{
        HttpUtil.ip
    }
}