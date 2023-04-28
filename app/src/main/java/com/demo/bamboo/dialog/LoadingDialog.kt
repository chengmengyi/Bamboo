package com.demo.bamboo.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.bamboo.R
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.layout_loading_dialog.*
import kotlinx.coroutines.*

class LoadingDialog(private val close:()->Unit): DialogFragment() {
    private var objectAnimator: ObjectAnimator?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.layout_loading_dialog,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        objectAnimator= ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }

        GlobalScope.launch {
            delay(2000L)
            withContext(Dispatchers.Main){
                dismiss()
                close.invoke()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator=null
    }
}