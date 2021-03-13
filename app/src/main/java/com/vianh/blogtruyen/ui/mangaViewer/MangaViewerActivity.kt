//package com.vianh.blogtruyen.ui.mangaViewer
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.GestureDetector
//import android.view.Gravity
//import android.view.MotionEvent
//import android.view.View
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
//import com.bumptech.glide.util.ViewPreloadSizeProvider
//import com.vianh.blogtruyen.BR
//import com.vianh.blogtruyen.R
//import com.vianh.blogtruyen.databinding.MangaViewerActivityBinding
//import com.vianh.blogtruyen.ui.base.BaseActivity
//import com.vianh.blogtruyen.utils.*
//
//
//class MangaViewerActivity : BaseActivity<MangaViewerViewModel, MangaViewerActivityBinding>() {
//    lateinit var detector: GestureDetector
//    lateinit var adapter: MangaViewerAdapter
//    val bitmapSize by lazy { getMaxTextureSize() }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setupViews()
//        observe()
//    }
//
//    private fun setupViews() {
//        val link = intent.getStringExtra("CHAPTER_LINK")
//        link?.let { getViewModel().getListImage(it) }
//        adapter = MangaViewerAdapter(getViewModel(), this)
//        detector = GestureDetector(this, TouchListener())
////        OverScrollDecoratorHelper.setUpOverScroll(getBinding().mangaRecycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
//
//        val layoutManager = PreCacheLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//
//        getBinding().mangaRecycler.apply {
//            setHasFixedSize(true)
//            setItemViewCacheSize(0)
//            setLayoutManager(layoutManager)
//            gestureDetector = detector
//            adapter = this@MangaViewerActivity.adapter
//        }
//        getBinding().toolbar.setBackgroundColor(Color.parseColor("#99000000"))
//    }
//
//    private fun observe() {
//        getViewModel().listImage.observe(this, Observer {
//            val sizeProvider = ViewPreloadSizeProvider<String>()
//            val preloader = RecyclerViewPreloader<String>(GlideApp.with(this), adapter, sizeProvider, 5)
//            getBinding().mangaRecycler.addOnScrollListener(preloader)
//        })
//    }
//
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) toggleSystemUI()
//    }
//
//    private fun toggleSystemUI() {
//        if (getBinding().toolbar.visibility == View.GONE) {
//            showSystemUI(window)
//            getBinding().toolbar.toggleState(Gravity.TOP)
//        } else {
//            hideSystemUI(window)
//            getBinding().toolbar.toggleState(Gravity.TOP)
//        }
//    }
//
//    override fun onDestroy() {
//        adapter.onDestroy()
//        super.onDestroy()
//    }
//
//    override fun getViewModelClass(): Class<MangaViewerViewModel> = MangaViewerViewModel::class.java
//
//    override fun getBindingVariable(): Int = BR.viewModel
//
//    override fun getLayoutId(): Int = R.layout.manga_viewer_activity
//
//    inner class TouchListener: GestureDetector.SimpleOnGestureListener() {
//        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
//            toggleSystemUI()
//            return true
//        }
//    }
//}
