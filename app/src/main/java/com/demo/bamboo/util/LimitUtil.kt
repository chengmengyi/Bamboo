package com.demo.bamboo.util

object LimitUtil {
    var isLimitUser=false

    fun checkLimit(){
        HttpUtil.requestIp {
            isLimitUser=HttpUtil.ip.limitArea()
        }
    }
}