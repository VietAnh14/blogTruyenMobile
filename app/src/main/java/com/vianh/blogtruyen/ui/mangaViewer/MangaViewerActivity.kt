package com.vianh.blogtruyen.ui.mangaViewer

import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaViewerActivityBinding
import com.vianh.blogtruyen.ui.base.BaseActivity
import com.vianh.blogtruyen.utils.PreCacheLayoutManager
import com.vianh.blogtruyen.utils.getDeviceHeight
import com.vianh.blogtruyen.utils.toggleState

class MangaViewerActivity : BaseActivity<MangaViewerViewModel, MangaViewerActivityBinding>() {
    lateinit var detector: GestureDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val link = intent.getStringExtra("CHAPTER_LINK")
        link?.let { getViewModel().getListImage(it) }
        val adapter = MangaViewerAdapter(getViewModel())
        detector = GestureDetector(this, TouchListener())

        val layoutManager = PreCacheLayoutManager(this)
//        layoutManager.extraSpace = 2*getDeviceHeight(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        getBinding().mangaRecycler.layoutManager = layoutManager
        getBinding().mangaRecycler.gestureDetector = detector
        getBinding().mangaRecycler.adapter = adapter
        getBinding().toolbar.setBackgroundColor(Color.parseColor("#99000000"))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) toggleSystemUI()
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        //Magic color that make status bar transparent (arbg) first 4 bits are alpha
        window.statusBarColor = Color.parseColor("#99000000")
        window.navigationBarColor = Color.parseColor("#99000000")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun toggleSystemUI() {
        if (getBinding().toolbar.visibility == View.GONE) {
            showSystemUI()
            getBinding().toolbar.toggleState(Gravity.TOP)
        } else {
            hideSystemUI()
            getBinding().toolbar.toggleState(Gravity.TOP)
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }


    override fun getViewModelClass(): Class<MangaViewerViewModel> = MangaViewerViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.manga_viewer_activity

    inner class TouchListener: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            toggleSystemUI()
            return true
        }
    }
}
