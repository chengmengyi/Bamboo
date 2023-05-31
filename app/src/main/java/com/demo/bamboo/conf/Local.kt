package com.demo.bamboo.conf

import com.demo.bamboo.bean.CountryBean
import com.demo.bamboo.bean.ServerBean

object Local {
    const val EMAIL=""
    const val WEB=""


    const val OPEN="bamboo_kai"
    const val CONNECT="bamboo_hello"
    const val BACK="bamboo_byte"
    const val HOME="bamboo_next"
    const val RESULT="bamboo_wai"

//    val localCountryList= arrayListOf(
//        CountryBean(
//            countryName = "Japan",
//            cityName = "LocalTokyo",
//            cityId = 0,
//            isLocal = true
//        ),
//        CountryBean(
//            countryName = "UnitedStates",
//            cityName = "LocalNewYork",
//            cityId = 0,
//            isLocal = true
//        ),
//    )
//
//
//    val localServerList= arrayListOf(
//        ServerBean(
//            bamboo_ip = "100.223.52.78",
//            bamboo_por = 100,
//            bamboo_pd = "123456",
//            bamboo_enc = "chacha20-ietf-poly1305",
//            bamboo_ry = "Japan",
//            bamboo_ci = "LocalTokyo",
//            isLocal = true
//        ),
//        ServerBean(
//            bamboo_ip = "100.223.52.77",
//            bamboo_por = 100,
//            bamboo_pd = "123456",
//            bamboo_enc = "chacha20-ietf-poly1305",
//            bamboo_ry = "UnitedStates",
//            bamboo_ci = "LocalNewYork",
//            isLocal = true
//        ),
//    )


    const val localAd3="""{
    "cat":1,
    "pretty":50,
    "bamboo_kai":[
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/3419835294x",
            "format":"open",
            "p":2
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/3419835294",
            "format":"open",
            "p":3
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/8691691433x",
            "format":"interstitial",
            "p":1
        }
    ],
    "bamboo_next":[
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/2247696110",
            "format":"native",
            "p":2
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/2247696110A",
            "format":"native",
            "p":3
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/1044960115",
            "format":"native",
            "p":1
        }
    ],
    "bamboo_wai":[
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/2247696110x",
            "format":"native",
            "p":1
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/2247696110",
            "format":"native",
            "p":3
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/1044960115x",
            "format":"native",
            "p":2
        }
    ],
    "bamboo_hello":[
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/1033173712",
            "format":"interstitial",
            "p":2
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/8691691433",
            "format":"interstitial",
            "p":1
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/8691691433XX",
            "format":"interstitial",
            "p":3
        }
    ],
    "bamboo_byte":[
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/1033173712",
            "format":"interstitial",
            "p":1
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/8691691433",
            "format":"interstitial",
            "p":2
        },
        {
            "resource":"admob",
            "id":"ca-app-pub-3940256099942544/8691691433XX",
            "format":"interstitial",
            "p":3
        }
    ]
}"""
}