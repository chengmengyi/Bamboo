package com.demo.bamboo.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.bamboo.interfaces.AppHomeInterface
import com.demo.bamboo.page.HomePage
import com.demo.bamboo.page.MainPage
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AppUtil {
    var isFront=true
    private var hotReload=false
    private var job: Job?=null
    private var appHomeInterface: AppHomeInterface?=null

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            job?.cancel()
            job=null
            if (pages==1){
                isFront=true
                if (hotReload){
                    if (ActivityUtils.isActivityExistsInStack(HomePage::class.java)){
                        activity.startActivity(Intent(activity, MainPage::class.java).apply {
                            putExtra("cold",false)
                        })
                    }
                }
                hotReload=false
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            pages--
            if (pages<=0){
                isFront=false
                appHomeInterface?.appToHome()
                job= GlobalScope.launch {
                    delay(3000L)
                    hotReload=true
                    ActivityUtils.finishActivity(MainPage::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }

    fun setIAppFrontInterface(appHomeInterface: AppHomeInterface?){
        this.appHomeInterface=appHomeInterface
    }
}