package com.demo.bamboo.conf

import com.demo.bamboo.bean.CountryBean
import com.demo.bamboo.bean.ServerBean

object Local {
    const val EMAIL="en2424l53@gmail.com"
    const val WEB="https://sites.google.com/view/bamboo-app/home"


    const val OPEN="bamboo_kai"
    const val CONNECT="bamboo_hello"
    const val BACK="bamboo_byte"
    const val HOME="bamboo_next"
    const val RESULT="bamboo_wai"

    val localCountryList= arrayListOf(
        CountryBean(
            countryName = "Japan",
            cityName = "LocalTokyo",
            cityId = 0,
            isLocal = true
        ),
        CountryBean(
            countryName = "UnitedStates",
            cityName = "LocalNewYork",
            cityId = 0,
            isLocal = true
        ),
    )


    val localServerList= arrayListOf(
        ServerBean(
            bamboo_ip = "185.194.216.196",
            bamboo_por = 5691,
            bamboo_pd = "9OuPVw#UBajyyzM",
            bamboo_enc = "chacha20-ietf-poly1305",
            bamboo_ry = "Germany",
            bamboo_ci = "Dusseldorf",
            isLocal = true
        )
    )


    const val localAd3="""{
    "pretty":30,
    "cat":5,
    "bamboo_kai":[
        {
            "resource":"admob",
            "format":"open",
            "id":"ca-app-pub-6337191878285963/8258596872",
            "p":1
        }
    ],
    "bamboo_next":[
        {
            "resource":"admob",
            "format":"native",
            "id":"ca-app-pub-6337191878285963/5155342261",
            "p":1
        }
    ],
    "bamboo_wai":[
        {
            "resource":"admob",
            "format":"native",
            "id":"ca-app-pub-6337191878285963/3842260590",
            "p":1
        }
    ],
    "bamboo_hello":[
        {
            "resource":"admob",
            "format":"interstitial",
            "id":"ca-app-pub-6337191878285963/8376401976",
            "p":1
        }
    ],
    "bamboo_byte":[
        {
            "resource":"admob",
            "format":"interstitial",
            "id":"ca-app-pub-6337191878285963/8376401976",
            "p":1
        }
    ]
}"""
}