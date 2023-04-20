package com.demo.bamboo.bean

class AdResultBean(
    var time:Long=0L,
    var ad:Any?=null
) {
    fun expired()=(System.currentTimeMillis() - time) >=3600L*1000
}