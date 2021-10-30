package com.vianh.blogtruyen.features.reader

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.PopupWindow
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.ReaderSettingPopupLayoutBinding

class SettingPopupWindow(context: Context, private val appSetting: AppSettings, val callback: Callback): PopupWindow() {
    val binding: ReaderSettingPopupLayoutBinding
    init {
        val inflater = LayoutInflater.from(context)
        binding = ReaderSettingPopupLayoutBinding.inflate(inflater)
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true

        with(binding) {
            setUpCheck(btnHorizon)
            setUpCheck(btnVertical)

            screenOnCb.isChecked = appSetting.getKeepScreenOn()
            screenOnCb.setOnCheckedChangeListener { _, isChecked ->
                appSetting.setKeepScreenOn(isChecked)
                callback.onKeepScreenOnChange(isChecked)
            }
        }
        checkReaderMode(appSetting.getReaderMode())

        setUpCheck(binding.btnHorizon)
        setUpCheck(binding.btnVertical)
    }

    private fun checkReaderMode(mode: ReaderMode) {
        clearCheck()
        when(mode) {
            ReaderMode.VERTICAL -> binding.btnVertical.isChecked = true
            ReaderMode.HORIZON -> binding.btnHorizon.isChecked = true
        }
    }

    private fun setUpCheck(view: CheckedTextView) {
        view.setOnClickListener {
            clearCheck()
            view.isChecked = true
            val readerMode = getReaderModeFromId(it.id)
            callback.onReaderModeSelected(readerMode)
            appSetting.setReaderMode(readerMode)

            dismiss()
        }
    }

    private fun getReaderModeFromId(id: Int): ReaderMode {
        return when(id) {
            R.id.btn_horizon -> ReaderMode.HORIZON
            else -> ReaderMode.VERTICAL
        }
    }

    private fun clearCheck() {
        binding.btnHorizon.isSelected = false
        binding.btnVertical.isSelected = false
    }

    companion object {
        fun show(anchor: View, appSetting: AppSettings, callback: Callback): SettingPopupWindow {
            val settingPopUp = SettingPopupWindow(anchor.context, appSetting, callback)
            settingPopUp.showAsDropDown(anchor, 0, 0, Gravity.TOP)
            return settingPopUp
        }
    }

    interface Callback {
        fun onReaderModeSelected(mode: ReaderMode)
        fun onKeepScreenOnChange(keepScreenOn: Boolean)
    }
}