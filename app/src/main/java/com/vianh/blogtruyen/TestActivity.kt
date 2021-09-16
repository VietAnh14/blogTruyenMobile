package com.vianh.blogtruyen

import android.os.Bundle
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {
        if (insets == null)
            return WindowInsetsCompat.CONSUMED

        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v?.updatePadding(top = systemInsets.top)
        return WindowInsetsCompat.CONSUMED
    }
}