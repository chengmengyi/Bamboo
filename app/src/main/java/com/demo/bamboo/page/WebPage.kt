package com.demo.bamboo.page

import com.demo.bamboo.R
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Local
import kotlinx.android.synthetic.main.activity_web.*

class WebPage:BasePage() {
    override fun layout(): Int = R.layout.activity_web

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        web_view.apply {
            settings.javaScriptEnabled=true
            loadUrl(Local.WEB)
        }
    }
}