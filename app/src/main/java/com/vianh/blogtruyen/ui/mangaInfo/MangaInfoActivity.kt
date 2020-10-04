package com.vianh.blogtruyen.ui.mangaInfo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.vianh.blogtruyen.BR
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaInfoActivityBinding
import com.vianh.blogtruyen.ui.base.BaseActivity
import com.vianh.blogtruyen.ui.mangaViewer.MangaViewerActivity
import kotlinx.android.synthetic.main.manga_info_activity.*

class MangaInfoActivity : BaseActivity<MangaInfoViewModel, MangaInfoActivityBinding>() {

    val binding by lazy { getBinding() }
    val viewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()
        val manga: Manga? = intent.getParcelableExtra("MANGA")
        manga?.let {
            viewModel.manga = it
            viewModel.loadData()
        }
    }

    private fun setUpViews() {
        val adapter = ChapterAdapter(viewModel)
        binding.chapterRecycler.setHasFixedSize(true)
        binding.chapterRecycler.adapter = adapter
        viewModel.chapters.observe(this, Observer { adapter.items = it })

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    if (appBarLayout.totalScrollRange + verticalOffset == 0) {
                        binding.collapsingToolbar.title = viewModel.mangaDetail.value?.title
                    } else {
                        binding.collapsingToolbar.title = " "
                    }
            })

        viewModel.chapterClickEvent.observe(this, Event.EventObserver {
            val intent = Intent(this, MangaViewerActivity::class.java)
            // TODO: Put all extra keys in another file
            adapter.notifyItemChanged(it.first)
            intent.putExtra("CHAPTER_LINK", it.second)
            startActivity(intent)
        })
        setUpMenu()
    }

    private fun setUpMenu() {
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.favorite) {
                toolbar.menu.getItem(0).setIcon(R.drawable.ic_favorite_black_24dp)
                true
            } else {
                false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manga_details_menu, menu)
        return true
    }

    override fun getViewModelClass(): Class<MangaInfoViewModel> = MangaInfoViewModel::class.java

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.manga_info_activity
}
