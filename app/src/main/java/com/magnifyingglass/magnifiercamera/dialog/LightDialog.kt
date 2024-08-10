package com.magnifyingglass.magnifiercamera.dialog

import android.content.Context
import android.view.View
import com.magnifyingglass.magnifiercamera.base.BaseRightDialog
import com.magnifyingglass.magnifiercamera.databinding.DialogLightBinding
import com.magnifyingglass.magnifiercamera.util.CustomSeekBar

class LightDialog(context: Context,private var light:Int = 0,private val listener:OnLightCallback): BaseRightDialog<DialogLightBinding>(context) {
    override fun initView(): View {
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.lightProgress.setCurrentDegrees(light.toFloat())
        binding.lightProgress.setOnProgressListener(object : CustomSeekBar.OnProgressListener{
            override fun onProgress(progress: Float) {

            }

            override fun onProgressNumber(progress: Float) {
                listener.onProgressNumber(progress)

            }
        })
        return binding.root
    }

    override fun dismiss() {
        super.dismiss()
        listener.close()
    }
    interface OnLightCallback{
        fun close()
        fun onProgressNumber(progress: Float)
    }
}