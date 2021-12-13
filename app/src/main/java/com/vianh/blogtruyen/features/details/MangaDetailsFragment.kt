package com.vianh.blogtruyen.features.details

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaDetailsFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.features.main.MainViewModel
import com.vianh.blogtruyen.utils.PendingIntentHelper
import com.vianh.blogtruyen.utils.loadNetWorkImage
import com.vianh.blogtruyen.utils.typeValue
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


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
            collapsingToolbar.title = manga.title
            toolbar.title = manga.title
            headerCover.loadNetWorkImage(manga.imageUrl)
            smallCover.loadImage(manga.imageUrl)
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

            tabLayout.doOnLayout {
                toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = it.height
                }
            }

            // Todo: Restore scroll offset
            when(viewModel.lastScroll) {
                0f -> appBarLayout.setExpanded(true)
                1f -> appBarLayout.setExpanded(false)
                else -> appBarLayout.setExpanded(true)
            }

            appBarLayout.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
                override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                    val progress = -verticalOffset.toFloat()/appBarLayout.totalScrollRange
                    viewModel.lastScroll = progress
                }
            })
        }
    }

    private val toolbarHeight by lazy { requireContext().typeValue(R.attr.actionBarSize).data }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        with(requireBinding) {
            val totalToolbarHeight = TypedValue.complexToDimensionPixelSize(toolbarHeight, resources.displayMetrics) + systemBarInsets.top
            toolbar.updateLayoutParams<ViewGroup.LayoutParams> { height = totalToolbarHeight }
            toolbar.updatePadding(top = systemBarInsets.top)
            pager.updatePadding(bottom = systemBarInsets.bottom)
            root.updatePadding(left = systemBarInsets.left, right = systemBarInsets.right)
        }
        return WindowInsetsCompat.CONSUMED
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