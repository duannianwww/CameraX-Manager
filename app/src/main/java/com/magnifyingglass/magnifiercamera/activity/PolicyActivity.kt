package com.magnifyingglass.magnifiercamera.activity

import com.magnifyingglass.magnifiercamera.base.BaseActivity
import com.magnifyingglass.magnifiercamera.databinding.ActivityPolicyBinding

class PolicyActivity: BaseActivity<ActivityPolicyBinding>() {
    override fun initView() {

    }

    override fun initListener() {
        binding.title.setOnClickListener {
            finish()
        }
    }

    override fun initData() {

    }
}