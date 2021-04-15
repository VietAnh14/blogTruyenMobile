package com.vianh.blogtruyen.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.HistoryFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment

class HistoryFragment: BaseFragment<HistoryFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HistoryFragmentBinding {
        return HistoryFragmentBinding.inflate(inflater, container, false)
    }
}