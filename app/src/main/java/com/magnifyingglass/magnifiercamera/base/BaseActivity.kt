package com.magnifyingglass.magnifiercamera.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.magnifyingglass.magnifiercamera.util.inflateWithGeneric

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = layoutInflater.inflateWithGeneric(this)
        setContentView(binding.root)
        initView()
        initListener()
        initData()
        val decorView: View = window.decorView
        val uiOptions: Int = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }
    protected abstract fun initView()
    protected abstract fun initListener()
    protected abstract fun initData()
}