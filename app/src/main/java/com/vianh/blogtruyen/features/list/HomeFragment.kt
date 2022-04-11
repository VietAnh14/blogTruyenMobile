package com.vianh.blogtruyen.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.features.search.SearchFragment
import com.vianh.blogtruyen.utils.DefaultSpanSizeLookup
import com.vianh.blogtruyen.utils.toPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: MangaFragment<HomeViewModel>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)

    override val title: String
        get() = getString(R.string.app_name)

    override val viewModel by viewModel<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    override fun applyInsets(insets: Insets): Boolean {
        super.applyInsets(insets)
        requireBinding.btnFilter.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = insets.right + 10.toPx
            bottomMargin = insets.bottom + 10.toPx
        }

        return true
    }

    private fun observe() {
        viewModel.pageReload.observe(viewLifecycleOwner, this::onPageReload)
        viewModel.error.observe(viewLifecycleOwner) { showToast(it.message) }
    }

    private fun onPageReload(reload: Boolean?) {
        requireBinding.swipeRefreshLayout.isRefreshing = reload ?: false
    }

    private fun setup() {
        with(requireBinding.feedRecycler) {
            val spanSizeLookup = DefaultSpanSizeLookup(this)
            spanSizeLookup.addViewType(MangaItem.MANGA_GRID_ITEM)
            spanSizeLookup.attachToParent()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        super.onMenuItemClick(item)

        when (item.itemId) {
            R.id.refresh -> {
                requireBinding.feedRecycler.scrollToPosition(0)
                viewModel.loadPage(1)
            }

            R.id.search_bar -> {
                hostActivity?.changeFragment(SearchFragment.newInstance(), true)
            }
            else -> return false
        }

        return true
    }

    override fun onClick(view: View, item: MangaItem) {
        val activity = activity as MainActivity
        activity.changeFragment(MangaDetailsFragment.newInstance(item.manga), true)
    }

    override fun onLongClick(view: View, item: MangaItem): Boolean {
        return false
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}