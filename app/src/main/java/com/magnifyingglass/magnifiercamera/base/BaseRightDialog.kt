package com.magnifyingglass.magnifiercamera.base

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.magnifyingglass.magnifiercamera.R
import com.magnifyingglass.magnifiercamera.util.inflateWithGeneric

abstract class BaseRightDialog<T : ViewBinding>(context: Context) :
    AlertDialog(context, R.style.CanterDialog) {
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = layoutInflater.inflateWithGeneric(this)
        setContentView(initView())
        window?.apply {
            setGravity(Gravity.END)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            setWindowAnimations(R.style.DialogAnimation)
        }
    }

    abstract fun initView(): View

    override fun dismiss() {
        if (isShowing) {
            super.dismiss()
        }
    }

    override fun show() {
        if (!isShowing) {
            super.show()
        }
    }
}