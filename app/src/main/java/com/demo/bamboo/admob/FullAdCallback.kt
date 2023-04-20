package com.demo.bamboo.admob

import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Local
import com.demo.bamboo.util.LimitUtil
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullAdCallback(
    private val type:String,
    private val basePage: BasePage,
    private val close:()->Unit
): FullScreenContentCallback() {

    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        LoadAdUtil.openAdShowing =false
        clickCloseAd()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        LoadAdUtil.openAdShowing  =true
        LimitUtil.updateShow()
        LoadAdUtil.removeAd(type)
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        LoadAdUtil.openAdShowing  =false
        LoadAdUtil.removeAd(type)
        clickCloseAd()
    }


    override fun onAdClicked() {
        super.onAdClicked()
        LimitUtil.updateClick()
    }

    private fun clickCloseAd(){
        if (type!= Local.OPEN&&type!= Local.BACK){
            LoadAdUtil.loadAd(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (basePage.resume){
                close.invoke()
            }
        }
    }
}