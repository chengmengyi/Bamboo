package com.demo.bamboo.page

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.demo.bamboo.R
import com.demo.bamboo.admob.LoadAdUtil
import com.demo.bamboo.admob.ShowFullAd
import com.demo.bamboo.admob.ShowNativeAd
import com.demo.bamboo.base.BasePage
import com.demo.bamboo.conf.Fire
import com.demo.bamboo.conf.Local
import com.demo.bamboo.interfaces.AppHomeInterface
import com.demo.bamboo.interfaces.ServerStatusInterface
import com.demo.bamboo.interfaces.ServerTimeInterface
import com.demo.bamboo.server.ServerInfo
import com.demo.bamboo.server.ServerTime
import com.demo.bamboo.server.ServerUtil
import com.demo.bamboo.server.ServerUtil.connectServerSuccess
import com.demo.bamboo.tba.SetPointUtil
import com.demo.bamboo.util.*
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_content.*
import kotlinx.android.synthetic.main.home_drawer.*
import kotlinx.coroutines.*
import java.lang.Exception

class HomePage:BasePage(), ServerStatusInterface, AppHomeInterface, ServerTimeInterface {
    private var time=-1
    private var canClick=true
    private var permission=false
    private var connect=false
    private var autoConnect=false
    private var connectServerJob: Job?=null
    private var objectAnimator: ObjectAnimator?=null

    private val showHomeAd by lazy { ShowNativeAd(Local.HOME,this) }
    private val showConnectAd by lazy { ShowFullAd(Local.CONNECT,this) }

    private val registerResult=registerForActivityResult(StartService()) {
        if (!it && permission) {
            permission = false
            checkServerIsFast()
            SetPointUtil.point("bamboo_v_get")
        } else {
            canClick=true
            showToast("Connected fail")
        }
    }

    override fun layout(): Int = R.layout.activity_home

    override fun initView() {
        immersionBar.statusBarView(top).init()
        ServerUtil.init(this,this)
        AppUtil.setIAppFrontInterface(this)
        updateServerInfoUI()
        ServerTime.setInterface(this)
        setClickListener()
        if (ServerUtil.isConnected()){
            hideGuide()
        }
        checkAuto(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { checkAuto(it) }
    }

    private fun checkAuto(intent: Intent){
        if(intent.getBooleanExtra("autoConnect",false)){
            autoConnect=true
            clickConnectBtn()
        }
    }


    private fun setClickListener(){
        iv_set.setOnClickListener {
            if(canClick&&!drawer_layout.isOpen){
                drawer_layout.openDrawer(Gravity.RIGHT)
            }
            if (!connect&&ServerUtil.isConnected()){
                appToHome()
            }
        }
        iv_choose_server.setOnClickListener {
            if(canClick&&!drawer_layout.isOpen){
                ServerInfo.checkToServerPage(supportFragmentManager){
                    startActivityForResult(Intent(this,ServerPage::class.java),417)
                }
            }
            if (!connect&&ServerUtil.isConnected()){
                appToHome()
            }
        }
        view_connect.setOnClickListener { clickConnectBtn() }
        llc_contact.setOnClickListener {
            try {
                val uri = Uri.parse("mailto:${Local.EMAIL}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                startActivity(intent)
            }catch (e: Exception){
                showToast("Contact us by email：${Local.EMAIL}")
            }
        }
        llc_update.setOnClickListener {
            val packName = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
            }
            startActivity(intent)
        }
        llc_agree.setOnClickListener { startActivity(Intent(this,WebPage::class.java)) }
        llc_share.setOnClickListener {
            val pm = packageManager
            val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            startActivity(Intent.createChooser(intent, "share"))
        }
        guide_view.setOnClickListener {  }
        guide_lottie_view.setOnClickListener { clickConnectBtn() }
    }

    private fun clickConnectBtn(){
        if(!autoConnect){
            SetPointUtil.point("bamboo_v_click")
        }
        hideGuide()
        if(LimitUtil.isLimitUser){
            AlertDialog.Builder(this).apply {
                setMessage("Due to the policy reason , this service is not available in your country")
                setPositiveButton("confirm", object :OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        finish()
                    }

                })
                show()
            }
            return
        }
        if (!canClick){
            return
        }
        canClick=false
        LoadAdUtil.loadAd(Local.CONNECT)
        LoadAdUtil.loadAd(Local.RESULT)
        if(ServerUtil.isConnected()){
            updateConnectingUI()
            startConnectServerJob(false)
        }else{
            updateServerInfoUI()
            if (getNetStatus()==1){
                AlertDialog.Builder(this).apply {
                    setMessage("Network request timed out. Please make sure your network is connected")
                    setPositiveButton("OK", null)
                    show()
                }
                canClick=true
                return
            }
            if (VpnService.prepare(this) != null) {
                permission = true
                registerResult.launch(null)
                return
            }

            checkServerIsFast()
        }
    }

    private fun checkServerIsFast(){
        if (ServerUtil.currentServer.isSuperFast()){
            ServerInfo.checkHasFast(supportFragmentManager){
                if(it){
                    startConnectServer()
                }else{
                    canClick=true
                }
            }
        }else{
            startConnectServer()
        }
    }

    private fun startConnectServer(){
        updateConnectingUI()
        ServerTime.resetTime()
        SetPointUtil.point("bamboo_v_start")
        startConnectServerJob(true)
    }

    private fun startConnectServerJob(connect:Boolean){
        time=0
        this.connect=connect
        connectServerJob= GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (!isActive) {
                    break
                }
                delay(1000)
                time++
                if (time==3){
                    if (connect){
                        ServerUtil.connect(autoConnect)
                        autoConnect=false
                    }else{
                        ServerUtil.disconnect()
                    }
                }

                if (time in 3..9){
                    if (connectServerSuccess(connect)){
                        if(null!=LoadAdUtil.getAdByType(Local.CONNECT)){
                            cancel()
                            showConnectAd.showFull(
                                showing = {
                                    connectJobFinish(connect,toResult = false)
                                },
                                close = {
                                    connectJobFinish(connect)
                                }
                            )
                        }else{
                            if(LimitUtil.hasLimit()||Fire.checkBlackLimitInterAd(Local.CONNECT)||Fire.checkFireConfigLimitInterAd()){
                                cancel()
                                connectJobFinish(connect)
                            }
                        }
                    }
                }else if (time >= 10) {
                    cancel()
                    stopObjectAnimator()
                    connectJobFinish(connect)
                }
            }
        }
    }

    private fun stopConnectServerJob(){
        connectServerJob?.cancel()
        connectServerJob=null
    }

    private fun connectJobFinish(connect: Boolean,toResult:Boolean=true){
        runOnUiThread {
            if (connectServerSuccess(connect)){
                if (connect){
                    if(toResult){
                        SetPointUtil.point("bamboo_v_win")
                    }
                    updateConnectedUI()
                }else{
                    updateStoppedUI()
                    updateServerInfoUI()
                }
                if(toResult&&connect){
                    ServerTime.start()
                }
                if (toResult&&AppUtil.isFront&&ActivityUtils.getTopActivity().javaClass.name==HomePage::class.java.name){
                    startActivity(Intent(this,ResultPage::class.java).apply {
                        putExtra("connect",connect)
                    })
                }
            }else{
                if (toResult){
                    SetPointUtil.point("bamboo_v_fail")
                }
                updateStoppedUI()
                showToast(if (connect) "Connect Fail" else "Disconnect Fail")
            }
            canClick=true
        }
    }


    private fun updateServerInfoUI(){
        val currentServer = ServerUtil.currentServer
        tv_name.text=currentServer.bamboo_ry
        iv_logo.setImageResource(getServerLogo(currentServer.bamboo_ry))
    }

    private fun updateConnectingUI(){
        startObjectAnimator()
        view_connect.isSelected=false
        iv_center.setImageResource(R.drawable.connect)
        iv_connect_status.setImageResource(R.drawable.home3)
    }

    private fun updateConnectedUI(){
        stopObjectAnimator()
        view_connect.isSelected=true
        tv_time.isSelected=true
        iv_center.setImageResource(R.drawable.connected)
        iv_connect_status.setImageResource(R.drawable.home4)
    }

    private fun updateStoppedUI(){
        stopObjectAnimator()
        view_connect.isSelected=false
        tv_time.isSelected=false
        tv_time.text="00:00:00"
        iv_center.setImageResource(R.drawable.connect)
        iv_connect_status.setImageResource(R.drawable.home2)
    }

    override fun connectSuccess() {
        updateConnectedUI()
    }

    override fun disconnectSuccess() {
        if (canClick) {
            updateStoppedUI()
        }
    }


    private fun startObjectAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(iv_connect_status, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode=ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopObjectAnimator(){
        objectAnimator?.cancel()
        objectAnimator=null
        iv_connect_status.rotation=0F
    }

    override fun appToHome() {
        if(time in 0..2){
            time=-1
            canClick=true
            stopConnectServerJob()
            if(connect){
                ServerUtil.state= BaseService.State.Stopped
                updateStoppedUI()
            }else{
                ServerUtil.currentServer=ServerUtil.lastServer
                ServerUtil.state= BaseService.State.Connected
                updateConnectedUI()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==417){
            when(data?.getStringExtra("action")){
                "dis"->{
                    clickConnectBtn()
                }
                "con"->{
                    updateServerInfoUI()
                    clickConnectBtn()
                }
            }
        }
    }


    override fun onBackPressed() {
        if(guideShowing()){
            hideGuide()
            return
        }
        if (canClick){
            finish()
        }
        if (!connect&&ServerUtil.isConnected()){
            appToHome()
        }
    }

    private fun guideShowing()=guide_lottie_view.visibility==View.VISIBLE

    private fun hideGuide(){
        guide_lottie_view.show(false)
        guide_view.show(false)
    }

    override fun onResume() {
        super.onResume()
        showHomeAd.loopCheckNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerUtil.onDestroy()
        stopObjectAnimator()
        stopConnectServerJob()
        AppUtil.setIAppFrontInterface(null)
        showHomeAd.stopLoop()
        Fire.coldLoad=false
        ServerTime.setInterface(this)
        LimitUtil.setRefreshStatus(Local.HOME,true)
    }

    override fun connectTime(time: String) {
        tv_time.text=time
    }

}