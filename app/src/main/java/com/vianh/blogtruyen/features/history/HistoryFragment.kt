package com.vianh.blogtruyen.features.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.HistoryFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment

class HistoryFragment: BaseFragment<HistoryFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HistoryFragmentBinding {
        return HistoryFragmentBinding.inflate(inflater, container, false)
    }
}