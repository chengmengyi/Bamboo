package com.demo.bamboo.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.bamboo.R
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.util.LimitUtil
import com.demo.bamboo.util.bambooLog
import com.demo.bamboo.util.show
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(
    private val type:String,
    private val basePage: BasePage,
) {
    private var lastNativeAd: NativeAd?=null
    private var showJob: Job?=null
    
    fun loopCheckNativeAd(){
        if(!LimitUtil.canRefresh(type)){
            return
        }
        LoadAdUtil.loadAd(type)
        stopLoop()
        showJob= GlobalScope.launch(Dispatchers.Main)  {
            delay(300L)
            if (!basePage.resume){
                return@launch
            }
            while (true){
                if(!isActive){
                    break
                }
                val ad = LoadAdUtil.getAdByType(type)
                if(basePage.resume&&null!=ad&&ad is NativeAd){
                    cancel()
                    lastNativeAd?.destroy()
                    lastNativeAd=ad
                    show(ad)
                }
                delay(1000L)
            }
        }
    }

    private fun show(ad:NativeAd){
        bambooLog("show $type ad ")
        val viewNative = basePage.findViewById<NativeAdView>(R.id.native_view)
        viewNative.iconView=basePage.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=basePage.findViewById(R.id.native_install)
        (viewNative.callToActionView as AppCompatTextView).text=ad.callToAction

        viewNative.mediaView=basePage.findViewById(R.id.native_media)
        ad.mediaContent?.let {
            viewNative.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(8F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }

        viewNative.bodyView=basePage.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body


        viewNative.headlineView=basePage.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        basePage.findViewById<AppCompatImageView>(R.id.iv_cover).show(false)
        viewNative.show(true)

        LimitUtil.updateShow()
        LoadAdUtil.removeAd(type)
        LoadAdUtil.loadAd(type)
        LimitUtil.setRefreshStatus(type,false)
    }


    fun stopLoop(){
        showJob?.cancel()
        showJob=null
    }
}