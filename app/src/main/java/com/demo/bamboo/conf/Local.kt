package com.demo.bamboo.conf

import com.demo.bamboo.bean.ServerBean

object Local {
    const val EMAIL="en2424l53@gmail.com"
    const val WEB="https://sites.google.com/view/bamboo-app/home"

    val localServerList= arrayListOf(
        ServerBean(
            ip = "185.194.216.196",
            port = 5691,
            pwd = "9OuPVw#UBajyyzM",
            account = "chacha20-ietf-poly1305",
            country = "Germany",
            city = "Dusseldorf",
        )
    )
}