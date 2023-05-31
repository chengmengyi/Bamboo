package com.demo.bamboo.server

import com.demo.bamboo.interfaces.ServerTimeInterface
import com.demo.bamboo.util.HttpUtil
import kotlinx.coroutines.*
import java.lang.Exception

object ServerTime {
    private var time=0L
    private var job: Job?=null
    private val interfaceList= arrayListOf<ServerTimeInterface>()

    fun setInterface(serverTimeInterface: ServerTimeInterface){
        if(interfaceList.contains(serverTimeInterface)){
            interfaceList.remove(serverTimeInterface)
        }else{
            interfaceList.add(serverTimeInterface)
        }
    }

    fun resetTime(){
        time=0L
    }

    fun start(){
        if (null!= job) return
        job = GlobalScope.launch(Dispatchers.Main) {
            while (null!=job) {
                interfaceList.forEach { it.connectTime(transTime(time)) }
                time++
                if(time%60==0L){
                    HttpUtil.checkHeartBeat(true)
                }
                delay(1000L)

            }
        }
    }

    fun end(){
        job?.cancel()
        job=null
    }

    fun getTimeInt()=time

    fun getTotalTime()=transTime(time)

    private fun transTime(t:Long):String{
        try {
            val shi=t/3600
            val fen= (t % 3600) / 60
            val miao= (t % 3600) % 60
            val s=if (shi<10) "0${shi}" else shi
            val f=if (fen<10) "0${fen}" else fen
            val m=if (miao<10) "0${miao}" else miao
            return "${s}:${f}:${m}"
        }catch (e: Exception){}
        return "00:00:00"
    }

}