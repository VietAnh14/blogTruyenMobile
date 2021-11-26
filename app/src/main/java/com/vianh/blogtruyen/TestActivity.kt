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
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.TestActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity
import com.vianh.blogtruyen.features.reader.SettingPopupWindow
import com.vianh.blogtruyen.utils.showToast

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
//        binding.btn.setOnClickListener {
////            SettingPopupWindow.show(it, AppSettings(this), this)
//            showPopupMenu(it)
//        }
//
//        with(binding.spannableText) {
//            text = getSpannableContent()
//            movementMethod = LinkMovementMethod.getInstance()
//            highlightColor = Color.TRANSPARENT
//        }
    }

    private fun showPopupMenu(view: View) {
        popupMenu {

            section {

                title = getString(R.string.settings)
                item {
                    dismissOnSelect = false
                    icon = R.drawable.ic_list_view
                    labelRes = R.string.vertical
                }

                item {
                    dismissOnSelect = false
                    icon = R.drawable.ic_continuous_vertical
                    labelRes = R.string.continuous_vertical
                }

                item {
                    dismissOnSelect = false
                    icon = R.drawable.ic_grid_view
                    labelRes = R.string.horizontal
                }
            }
        }.show(view.context, view)
    }

    fun getSpannableContent(): CharSequence {
        return buildSpannedString {
            italic {
                bold { append("Vi Anh") }
            }
            append(" ")
            append("dep trai vcl", TextClickableSpan("dep trai vcl"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    inner class TextClickableSpan(val origin: String): ClickableSpan() {
        override fun onClick(p0: View) {
            showToast(origin)
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