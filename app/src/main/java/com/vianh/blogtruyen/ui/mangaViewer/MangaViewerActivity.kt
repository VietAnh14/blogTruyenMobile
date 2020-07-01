package com.vianh.blogtruyen.ui.mangaViewer

import android.os.Bundle
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaViewerActivityBinding
import com.vianh.blogtruyen.ui.base.BaseActivity

class MangaViewerActivity : BaseActivity<MangaViewerViewModel, MangaViewerActivityBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val link = intent.getStringExtra("CHAPTER_LINK")
        link?.let { getViewModel().getListImage(it) }
        val adapter = MangaViewerAdapter(getViewModel())
        getBinding().mangaRecycler.adapter = adapter
        getBinding().mangaRecycler.setItemViewCacheSize(20)
    }

    override fun getViewModelClass(): Class<MangaViewerViewModel> = MangaViewerViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.manga_viewer_activity
}
