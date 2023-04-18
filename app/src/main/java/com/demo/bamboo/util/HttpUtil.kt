package com.demo.bamboo.util

import android.webkit.WebView
import com.demo.bamboo.bambooApp
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

object HttpUtil {
    var ip=""
    var countryCode=""

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
}