package com.demo.bamboo.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import com.demo.bamboo.R
import com.demo.bamboo.admob.LoadAdUtil
import com.demo.bamboo.admob.ShowFullAd
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.conf.Fire.coldLoad
import com.demo.bamboo.conf.Local
import com.demo.bamboo.server.ServerUtil
import com.demo.bamboo.tba.SetPointUtil
import com.demo.bamboo.util.HttpUtil
import com.demo.bamboo.util.LimitUtil
import com.demo.bamboo.util.ReferUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainPage : BasePage() {
    private var animator: ValueAnimator?=null
    private val showOpenAd by lazy { ShowFullAd(Local.OPEN,this) }

    override fun layout(): Int = R.layout.activity_main

    override fun initView() {
        if(coldLoad){
            coldLoad=intent.getBooleanExtra("cold",true)
        }
        HttpUtil.getServerList()
        HttpUtil.checkCloak()
        ReferUtil.readReferrer()
        LimitUtil.checkLimit()
        LimitUtil.resetRefresh()
        LimitUtil.readNum()
        LoadAdUtil.preloadAd()
        startAnimator()
    }

    private fun startAnimator(){
        animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if(pro in 2..9){
                    showOpenAd.showFull(
                        showing = {
                            stopAnimator()
                            progress_view.progress = 100
                        },
                        close = {
                            checkPlan()
                        }
                    )
                }else if (pro>=10){
                    checkPlan()
                }
            }
            start()
        }
    }

    private fun checkPlan(){
        if(ReferUtil.isBuyUser()||ReferUtil.isFB()){
            Fire.checkIsPlanB()
            if(Fire.isPlanB){
                SetPointUtil.point("bamboo_plan")
            }
            toHomeAc(autoConnect = Fire.isPlanB&&ServerUtil.isDisconnected())
        }else{
            toHomeAc()
        }
    }

    private fun toHomeAc(autoConnect:Boolean=false){
        startActivity(Intent(this, HomePage::class.java).apply {
            putExtra("autoConnect",autoConnect)
        })
        finish()
    }

    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
    }

    override fun onResume() {
        super.onResume()
        if (animator?.isPaused==true){
            animator?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        animator?.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }

    override fun onBackPressed() {}
}