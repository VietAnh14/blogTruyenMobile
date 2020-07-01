package com.vianh.blogtruyen.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ActivityMainBinding
import com.vianh.blogtruyen.ui.base.BaseActivity
import com.vianh.blogtruyen.ui.mangaInfo.MangaInfoActivity
import com.vianh.blogtruyen.utils.GridItemSpacingDecorator
import com.vianh.blogtruyen.utils.ScrollLoadMore

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    val viewModel by lazy {
        getViewModel()
    }
    val binding by lazy {
        getBinding()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecycler()
        viewModel.mangaClickEvent.observe(this, Event.EventObserver {
            val intent = Intent(this, MangaInfoActivity::class.java).apply {
                putExtra("MANGA", it)
            }
            startActivity(intent)
        })
    }

    private fun setupRecycler() {
        val spacing = resources.getDimensionPixelSize(R.dimen.app_margin)
        binding.mangaDashboardRecycler.setHasFixedSize(true)
        binding.mangaDashboardRecycler.adapter = DashBoardAdapter(mutableListOf(), viewModel)
        binding.mangaDashboardRecycler.addItemDecoration(GridItemSpacingDecorator(spacing))
        viewModel.getListManga()
        viewModel._items.observe(this, Observer {
            val adapter = binding.mangaDashboardRecycler.adapter as DashBoardAdapter
            adapter.addItems(it)
        })

        // scroll to load more
        binding.mangaDashboardRecycler
            .addOnScrollListener(ScrollLoadMore(4) {viewModel.getListManga()})
    }

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_main
}
