package com.demo.bamboo.util

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.demo.bamboo.BuildConfig
import com.demo.bamboo.bambooApp
import com.tencent.mmkv.MMKV

object ReferUtil {
    fun readReferrer(){
        if(readLocalReferrer().isEmpty()){
            val referrerClient = InstallReferrerClient.newBuilder(bambooApp).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val installReferrer = referrerClient.installReferrer.installReferrer
                                MMKV.defaultMMKV().encode("referrer_bamboo",installReferrer)
                            }
                            else->{}
                        }
                    }
                    kotlin.runCatching {
                        referrerClient.endConnection()
                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }
    }

    fun isBuyUser():Boolean{
        if (BuildConfig.DEBUG){
            return true
        }
        val localReferrer = readLocalReferrer()
        return localReferrer.contains("fb4a")||
                localReferrer.contains("gclid")||
                localReferrer.contains("not%20set")||
                localReferrer.contains("youtubeads")||
                localReferrer.contains("%7B%22")
    }


    fun isFB():Boolean{
        if (BuildConfig.DEBUG){
            return true
        }
        val localReferrer = readLocalReferrer()
        return localReferrer.contains("fb4a")|| localReferrer.contains("facebook")
    }

    private fun readLocalReferrer()= MMKV.defaultMMKV().decodeString("referrer_bamboo")?:""
}