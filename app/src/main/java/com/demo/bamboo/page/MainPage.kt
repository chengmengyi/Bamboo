package com.demo.bamboo.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.bamboo.R
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.util.LimitUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainPage : BasePage() {
    private var animator: ValueAnimator?=null

    override fun layout(): Int = R.layout.activity_main

    override fun initView() {
        LimitUtil.checkLimit()
        startAnimator()
    }

    private fun startAnimator(){
        animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 3000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()

            }
            doOnEnd { toHomeAc() }
            start()
        }
    }

    private fun checkPlan(){
//        if(!ReferrerUtil.isBuyUser()){
//            toHomeAc()
//            return
//        }
//        FireConf.checkIsPlanB(coldLoad)
//        PointSet.setUserProperty()
//        toHomeAc(autoConnect = FireConf.isPlanB&&ConnectUtil.isDisconnected())
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