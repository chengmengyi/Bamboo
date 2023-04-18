package com.demo.bamboo.conf

import com.demo.bamboo.bean.ServerBean

object Local {
    const val EMAIL=""
    const val WEB=""

    val localServerList= arrayListOf(
        ServerBean(
            ip = "100.223.52.78",
            port = 100,
            pwd = "123456",
            account = "chacha20-ietf-poly1305",
            country = "Japan",
            city = "LocalTokyo",
        ),
        ServerBean(
            ip = "100.223.52.77",
            port = 100,
            pwd = "123456",
            account = "chacha20-ietf-poly1305",
            country = "UnitedStates",
            city = "LocalNewYork",
        ),
    )
}