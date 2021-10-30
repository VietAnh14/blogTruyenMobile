package com.vianh.blogtruyen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.*
import com.google.android.material.appbar.AppBarLayout
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.TestActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity
import com.vianh.blogtruyen.features.reader.SettingPopupWindow
import timber.log.Timber

class TestActivity: BaseActivity<TestActivityBinding>(), OnApplyWindowInsetsListener, SettingPopupWindow.Callback{
    override fun createBinding(): TestActivityBinding {
        return TestActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, this)
        setUp()
    }

    fun setUp() {
        binding.btn.setOnClickListener {
            SettingPopupWindow.show(it, AppSettings(this), this)
        }
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {
        if (insets == null)
            return WindowInsetsCompat.CONSUMED

        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v?.updatePadding(top = systemInsets.top)
        return WindowInsetsCompat.CONSUMED
    }

    override fun onReaderModeSelected(mode: ReaderMode) {

    }

    override fun onKeepScreenOnChange(keepScreenOn: Boolean) {

    }
}