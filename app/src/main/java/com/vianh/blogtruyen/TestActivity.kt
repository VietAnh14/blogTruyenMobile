package com.vianh.blogtruyen

import android.os.Bundle
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
//import com.github.zawadz88.materialpopupmenu.popupMenu
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.TestActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity
import com.vianh.blogtruyen.features.reader.SettingPopupWindow
import com.vianh.blogtruyen.utils.showToast

class TestActivity: BaseActivity<TestActivityBinding>() {
    override fun createBinding(): TestActivityBinding {
        return TestActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(binding.toolbar)
    }
}