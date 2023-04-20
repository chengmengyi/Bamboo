package com.demo.bamboo.admob

import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.conf.Local
import com.demo.bamboo.util.bambooLog
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd

class ShowFullAd(
    private val type:String,
    private val basePage: BasePage
) {

    fun showFull(emptyBack:Boolean=false,showing:()->Unit,close:()->Unit){
        if(type!=Local.OPEN){
            if(Fire.checkBlackLimitInterAd(type)||Fire.checkFireConfigLimitInterAd()){
                close.invoke()
                return
            }
        }
        val ad = LoadAdUtil.getAdByType(type)
        if (null!=ad){
            if (LoadAdUtil.openAdShowing||!basePage.resume){
                close.invoke()
                return
            }
            bambooLog("show $type ad")
            showing.invoke()
            when(ad){
                is InterstitialAd ->{
                    ad.fullScreenContentCallback= FullAdCallback(type,basePage,close)
                    ad.show(basePage)
                }
                is AppOpenAd ->{
                    ad.fullScreenContentCallback= FullAdCallback(type,basePage,close)
                    ad.show(basePage)
                }
            }
        }else{
            if (emptyBack){
                LoadAdUtil.loadAd(type)
                close.invoke()
            }
        }
    }
}