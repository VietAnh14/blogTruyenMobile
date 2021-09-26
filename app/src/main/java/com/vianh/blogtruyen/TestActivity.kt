package com.vianh.blogtruyen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import com.google.android.material.appbar.AppBarLayout
import com.vianh.blogtruyen.databinding.TestActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity

class TestActivity: BaseActivity<TestActivityBinding>(), OnApplyWindowInsetsListener {
    override fun createBinding(): TestActivityBinding {
        return TestActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.topAppBar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBar, this)
        setUp()
    }

    fun setUp() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            binding.appbar.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
//                override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
//                    val progress = 1 + verticalOffset/appBarLayout.totalScrollRange.toFloat()
//                    if (progress in 0f..1f) {
//                        Log.d("TAG", "onOffsetChanged: $progress")
//                        binding.header.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = (progress*500).toInt() }
//                    }
//                }
//            })
//        }, 1000)
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {
        if (insets == null)
            return WindowInsetsCompat.CONSUMED

        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v?.updatePadding(top = systemInsets.top)
        return WindowInsetsCompat.CONSUMED
    }
}