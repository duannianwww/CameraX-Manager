package com.magnifyingglass.magnifiercamera.activity

import android.content.Intent
import com.magnifyingglass.magnifiercamera.base.BaseActivity
import com.magnifyingglass.magnifiercamera.databinding.ActivitySettingBinding
import com.magnifyingglass.magnifiercamera.util.goToMarket
import com.magnifyingglass.magnifiercamera.util.sendMail
import com.magnifyingglass.magnifiercamera.util.shareToFriend

class SettingActivity: BaseActivity<ActivitySettingBinding>() {
    override fun initView() {

    }

    override fun initListener() {
        binding.rate.setOnClickListener {
            packageName.goToMarket(this)
        }
        binding.policy.setOnClickListener {
            startActivity(Intent(this,PolicyActivity::class.java))
        }
        binding.share.setOnClickListener {
            packageName.shareToFriend(this)
        }
        binding.feedback.setOnClickListener {
            "".sendMail(this)
        }
        binding.constraintLayout.setOnClickListener {
            finish()
        }
    }

    override fun initData() {

    }
}