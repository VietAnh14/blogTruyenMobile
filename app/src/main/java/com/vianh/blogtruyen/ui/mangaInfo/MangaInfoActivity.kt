package com.vianh.blogtruyen.ui.mangaInfo

import android.os.Bundle
import androidx.lifecycle.Observer
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaInfoActivityBinding
import com.vianh.blogtruyen.ui.base.BaseActivity

class MangaInfoActivity : BaseActivity<MangaInfoViewModel, MangaInfoActivityBinding>() {

    val binding by lazy { getBinding() }
    val viewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()
        viewModel.loadData(intent.getParcelableExtra("MANGA")!!)
    }

    private fun setUpViews() {
        val adapter = ChapterAdapter()
        binding.chapterRecycler.setHasFixedSize(true)
        binding.chapterRecycler.adapter = adapter
        viewModel.chapters.observe(this, Observer { adapter.items = it })
    }

    override fun getViewModelClass(): Class<MangaInfoViewModel> = MangaInfoViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.manga_info_activity
}
