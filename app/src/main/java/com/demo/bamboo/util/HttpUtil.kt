package com.demo.bamboo.util

import android.util.Base64
import android.util.Log
import android.webkit.WebView
import androidx.fragment.app.FragmentManager
import com.demo.bamboo.bambooApp
import com.demo.bamboo.bean.CountryBean
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.conf.Local.localCountryList
import com.demo.bamboo.tba.TbaInfo
import com.demo.newvpn.dialog.LoadingDialog
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object HttpUtil {
    var ip=""
    var countryCode=""
    private val countryList= arrayListOf<CountryBean>()

    private const val TBA_URL="https://little.bambooconnection.net/buddhist/trespass/allergy/liaison"
    private const val SERVER_URL="https://test.superfastbamboo.com"
    private const val CLOAK_URL="https://decry.bambooconnection.net/calendar/woven/parka"

    fun requestIp(callback:()->Unit){
        if(ip.isNotEmpty()){
            callback.invoke()
            return
        }
        OkGo.get<String>("https://ipapi.co/json")
            .headers("User-Agent", WebView(bambooApp).settings.userAgentString)
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    runCatching {
                        val jsonObject = JSONObject(response?.body()?.toString())
                        countryCode=jsonObject.optString("country_code")
                        ip=jsonObject.optString("ip")
                    }
                    callback.invoke()
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    callback.invoke()                }
            })
    }

    fun uploadEvent(jsonObject: JSONObject,install:Boolean=false){
        val path="$TBA_URL?befallen=${System.currentTimeMillis()}&incite=${TbaInfo.getAndroidId(bambooApp)}&gershwin=${TbaInfo.getBrand()}"
        Log.e("qwera",jsonObject.toString())
        var gaid=""
        runCatching {
            gaid=jsonObject.getJSONObject("coulter").optString("comply")
        }
        OkGo.post<String>(path)
            .retryCount(3)
            .headers("content-type","application/json")
            .headers("comply", gaid)
            .headers("hairdo", "weston")
            .upJson(jsonObject)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    if (install){
                        if (jsonObject.getJSONObject("intrepid").optString("eventual").isEmpty()){
                            saveNoReferrerTag()
                        }else{
                            saveHasReferrerTag()
                        }
                    }
                    bambooLog("=onSuccess==${response?.body()?.toString()}==")
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    bambooLog("=onError==${response?.body()?.toString()}==")
                }
            })
    }


    fun checkCloak(){
        requestIp {
            GlobalScope.launch {
                val gaid = TbaInfo.getGaid(bambooApp)
                val path="$CLOAK_URL?upgrade=${TbaInfo.getDistinctId(bambooApp)}&lumbago=${ip}&rode=${System.currentTimeMillis()}&alderman=${TbaInfo.getDeviceModel()}&manage=${TbaInfo.getBundleId(
                    bambooApp)}&enoch${TbaInfo.getOsVersion()}&comply=$gaid&incite=${TbaInfo.getAndroidId(
                    bambooApp)}&hairdo=weston&embolden=${TbaInfo.getAppVersion(bambooApp)}&gershwin=${TbaInfo.getBrand()}&lewd=${TbaInfo.getOperator(
                    bambooApp)}"
                OkGo.get<String>(path)
                    .execute(object : StringCallback(){
                        override fun onSuccess(response: Response<String>?) {
                            bambooLog("=checkCloak==onSuccess==${response?.body()?.toString()}===")
                            Fire.isBlack=response?.body()?.toString()=="phylum"
                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            bambooLog("=checkCloak===onError=${response?.body()?.toString()}===")
                        }
                    })
            }
        }
    }

    fun getServerList(manager: FragmentManager, callback: (list:ArrayList<CountryBean>) -> Unit){
        if(countryList.isEmpty()){
            var loadingDialog= LoadingDialog()
            loadingDialog.show(manager,"LoadingDialog")
            OkGo.get<String>("$SERVER_URL/swmz/sok/")
                .headers("QML", TbaInfo.getOsCountry())
                .headers("SOA", bambooApp.packageName)
                .headers("LASO",TbaInfo.getAndroidId(bambooApp))
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        loadingDialog.dismiss()
                        decodeServerStr(response?.body()?.toString()?:"")
//                        parseServerListJson(decodeServerStr(response?.body()?.toString()?:""))
                        if(countryList.isEmpty()){
                            callback.invoke(localCountryList)
                        }else{
                            callback.invoke(countryList)
                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        loadingDialog.dismiss()
                        callback.invoke(localCountryList)
                    }
                })
        }else{
            callback.invoke(countryList)
        }
    }


    private fun decodeServerStr(string: String):String{
        var stringBuffer = StringBuffer(string)
        bambooLog("=====kk:${stringBuffer.toString()}")
        runCatching {
            stringBuffer=stringBuffer.delete(0,3).delete(stringBuffer.length-4,stringBuffer.length)
            String(Base64.decode(stringBuffer.reverse().toString(), Base64.DEFAULT))
            bambooLog("=====kk:${stringBuffer.toString()}")
        }
        return stringBuffer.toString()
    }
}