package com.vianh.blogtruyen.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment

class HomeFragment: BaseFragment<HomeFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)
}