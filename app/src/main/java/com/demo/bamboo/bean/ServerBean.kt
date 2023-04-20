package com.demo.bamboo.bean

import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager

class ServerBean(
    val bamboo_pd:String="",
    val bamboo_enc:String="",
    val bamboo_por:Int=0,
    val bamboo_ry:String="Smart Location",
    val bamboo_ci:String="",
    val bamboo_ip:String="",
    val isLocal:Boolean=false
) {

    fun isSuperFast()=bamboo_ry=="Smart Location"&&bamboo_ip.isEmpty()

    fun getServerId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==bamboo_ip&&it.remotePort==bamboo_por){
                return it.id
            }
        }
        return 0L
    }

    fun writeServerId(){
        val profile = Profile(
            id = 0L,
            name = "$bamboo_ry - $bamboo_ci",
            host = bamboo_ip,
            remotePort = bamboo_por,
            password = bamboo_pd,
            method = bamboo_enc
        )

        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }

}