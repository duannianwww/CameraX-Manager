package com.magnifyingglass.magnifiercamera

import android.app.Application
import android.content.Context

class MagnifierApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object{
        lateinit var mContext:Application
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}