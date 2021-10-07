package com.vianh.blogtruyen.features.details

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.createCab
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaDetailsFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.features.main.MainViewModel
import com.vianh.blogtruyen.utils.PendingIntentHelper
import com.vianh.blogtruyen.utils.await
import com.vianh.blogtruyen.utils.loadNetWorkImage
import com.vianh.blogtruyen.utils.toSafeFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.sink
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.core.view.doOnLayout
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams


class MangaDetailsFragment : BaseFragment<MangaDetailsFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): MangaDetailsFragmentBinding = MangaDetailsFragmentBinding.inflate(inflater, container, false)

    private val activityViewModel by sharedViewModel<MainViewModel>()
    private val viewModel: MangaDetailsViewModel by viewModel {
        parametersOf(getManga(), getOfflineState())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.manga.observe(viewLifecycleOwner, ::onContentChange)
    }

    private fun onContentChange(manga: Manga) {
        with(requireBinding) {
            toolbar.title = manga.title
            headerCover.loadNetWorkImage(manga.imageUrl)
            smallCover.loadNetWorkImage(manga.imageUrl)
            mangaTitle.text = manga.title
        }
    }

    private fun setup() {
        setupToolbar(requireBinding.toolbar, menuId = R.menu.manga_details_menu)
        with(requireBinding) {
            pager.adapter = ContentPagerAdapter(this@MangaDetailsFragment)
            TabLayoutMediator(tabLayout, pager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.manga_info)
                    1 -> getString(R.string.comment)
                    else -> "Unknown"
                }
            }.attach()

            pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.selectPage(position)
                }
            })

            toolbarContainer.doOnLayout {
                headerContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = -it.height
                }
            }

            // Todo: Restore scroll offset
            when(viewModel.lastScroll) {
                0f -> appBarLayout.setExpanded(true)
                1f -> appBarLayout.setExpanded(false)
            }

            appBarLayout.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
                override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                    val progress = -verticalOffset.toFloat()/appBarLayout.totalScrollRange
                    toolbarBg.alpha = progress
                    viewModel.lastScroll = progress
                }
            })
        }
    }



    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_cover -> {
                activityViewModel.saveMangaCover(viewModel.currentManga, requireContext().applicationContext)
                true
            }

            else -> super.onMenuItemClick(item)
        }
    }

    private fun getManga(): Manga {
        val manga: Manga? = arguments?.getParcelable(MANGA_BUNDLE_KEY)
        return if (manga == null) {
            showToast("Failed to get manga details :(")
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            Manga.getEmpty()
        } else {
            manga
        }
    }

    private fun getOfflineState(): Boolean {
        return arguments?.getBoolean(OFFLINE_MODE_KEY) ?: false
    }

    companion object {
        const val MANGA_BUNDLE_KEY = "MANGA_KEY"
        const val OFFLINE_MODE_KEY = "OFFLINE"
        const val APPBAR_SCROLL_KEY = "app_bar_scroll"

        fun newInstance(manga: Manga, isOffline: Boolean = false): MangaDetailsFragment {
            val bundle = Bundle(2).apply {
                putParcelable(MANGA_BUNDLE_KEY, manga)
                putBoolean(OFFLINE_MODE_KEY, isOffline)
            }
            return MangaDetailsFragment().apply {
                arguments = bundle
            }
        }

        fun getPendingIntent(context: Context, manga: Manga, isOffline: Boolean = false): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(PendingIntentHelper.UPDATE_INTENT_FLAGS)
                action = MainActivity.ACTION_DOWNLOAD_COMPLETE
                putExtra(MANGA_BUNDLE_KEY, manga)
                putExtra(OFFLINE_MODE_KEY, isOffline)
            }

            return PendingIntent.getActivity(context, 0, intent, PendingIntentHelper.getPendingFlagCompat())
        }
    }
}