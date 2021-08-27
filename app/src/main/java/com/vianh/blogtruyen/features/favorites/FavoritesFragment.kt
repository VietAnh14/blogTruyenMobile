package com.vianh.blogtruyen.features.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.BookmarksFragmentBinding
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.list.MangaListAdapter
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.features.list.MangaItemVH
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment: BaseFragment<HomeFragmentBinding>(), MangaItemVH.MangaClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var listAdapter: MangaListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        bindViewModel()
    }

    private fun setup() {
        setHasOptionsMenu(true)

        with(requireBinding) {
            hostActivity?.setupToolbar(toolbar, getString(R.string.bookmarks))

            feedRecycler.apply {
                setHasFixedSize(true)
                adapter = MangaListAdapter(this@FavoritesFragment).also {
                    listAdapter = it
                }
            }

            swipeRefreshLayout.isEnabled = false
        }
    }

    private fun bindViewModel() {
        viewModel.content.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }

    override fun onMangaItemClick(mangaItem: MangaItem) {
        val detailsFragment = MangaDetailsFragment.newInstance(mangaItem.manga)
        hostActivity?.changeFragment(detailsFragment, true)
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}