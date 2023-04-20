package com.demo.bamboo.admob

import com.demo.bamboo.bean.AdBean
import com.demo.bamboo.bean.AdResultBean
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.conf.Local
import com.demo.bamboo.util.LimitUtil
import com.demo.bamboo.util.bambooLog
import org.json.JSONObject

object LoadAdUtil :BaseLoad() {
    var openAdShowing=false
    private val loading= arrayListOf<String>()
    private val adResultMap= hashMapOf<String, AdResultBean>()
    
    fun loadAd(type:String,tryNum:Int=0){
        if(LimitUtil.hasLimit()){
            bambooLog("limit")
            return
        }
        if(Fire.checkBlackLimitInterAd(type)){
            bambooLog("black limit")
            return
        }
        if(loading.contains(type)){
            bambooLog("$type  loading")
            return
        }

        if(adResultMap.containsKey(type)){
            val resultAdBean = adResultMap[type]
            if(null!=resultAdBean?.ad){
                if(resultAdBean.expired()){
                    removeAd(type)
                }else{
                    bambooLog("$type cache")
                    return
                }
            }
        }
        val adList = getAdListByType(type)
        if(adList.isEmpty()){
            return
        }
        loading.add(type)
        loopLoadAd(type,adList.iterator(),tryNum)
    }

    private fun loopLoadAd(type: String, iterator: Iterator<AdBean>, tryNum:Int){
        loadAdByType(type,iterator.next()){
            if(null!=it){
                loading.remove(type)
                adResultMap[type]=it
            }else{
                if(iterator.hasNext()){
                    loopLoadAd(type,iterator,tryNum)
                }else{
                    loading.remove(type)
                    if(tryNum>0){
                        loadAd(type, tryNum = 0)
                    }
                }
            }
        }
    }
    
    private fun getAdListByType(type: String):List<AdBean>{
        val list= arrayListOf<AdBean>()
        runCatching {
            val jsonArray = JSONObject(Fire.getAdStr()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    AdBean(
                        jsonObject.optString("id"),
                        jsonObject.optString("resource"),
                        jsonObject.optString("format"),
                        jsonObject.optInt("p"),
                    )
                )
            }
        }
        return list.filter { it.bambooFrom == "admob" }.sortedByDescending { it.bambooSort }
    }
    
    fun removeAd(type: String){
        adResultMap.remove(type)
    }

    fun getAdByType(type: String)= adResultMap[type]?.ad

    fun preloadAd(){
        loadAd(Local.OPEN)
        loadAd(Local.CONNECT)
        loadAd(Local.HOME)
        loadAd(Local.RESULT)
    }

    fun connectSuccessPlanB(){
        adResultMap.clear()
        loading.clear()
        preloadAd()
        loadAd(Local.BACK)
    }
}