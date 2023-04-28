package com.demo.bamboo.tba

import android.os.Bundle
import com.demo.bamboo.BuildConfig
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.util.bambooLog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object SetPointUtil {
    private var remoteConfig: FirebaseAnalytics?=null
    init {
        if (!BuildConfig.DEBUG){
            remoteConfig= Firebase.analytics
        }
    }

    fun point(key:String,bundle: Bundle = Bundle()){
        bambooLog("point==$key==")
        remoteConfig?.logEvent(key,bundle)
    }
}