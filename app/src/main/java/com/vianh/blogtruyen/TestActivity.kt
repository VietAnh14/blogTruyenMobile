package com.vianh.blogtruyen

import android.os.Bundle
//import com.github.zawadz88.materialpopupmenu.popupMenu
import com.vianh.blogtruyen.databinding.TestActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity

class TestActivity: BaseActivity<TestActivityBinding>() {
    override fun createBinding(): TestActivityBinding {
        return TestActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(binding.toolbar)
    }
}