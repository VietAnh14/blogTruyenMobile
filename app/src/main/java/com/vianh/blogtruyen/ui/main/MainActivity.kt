package com.vianh.blogtruyen.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ActivityMainBinding
import com.vianh.blogtruyen.ui.base.BaseActivity

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    val viewModel by lazy {
        getViewModel()
    }
    val binding by lazy {
        getBinding()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mangaDashboardRecycler.adapter = DashBoardAdapter(mutableListOf())
        viewModel.getListManga()
        viewModel._items.observe(this, Observer {
            val adapter = binding.mangaDashboardRecycler.adapter as DashBoardAdapter
            adapter.setItems(it)
        })
    }

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_main
}
