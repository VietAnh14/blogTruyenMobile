package com.vianh.blogtruyen.features.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.features.list.MangaListAdapter
import com.vianh.blogtruyen.views.recycler.DefaultSpanSizeLookup
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment: BaseFragment<HomeFragmentBinding>(), MangaListAdapter.Callback, SearchView.OnQueryTextListener {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel: FavoriteViewModel by viewModel()
    private var listAdapter: MangaListAdapter? = null

    override fun getToolbar(): Toolbar? {
        return requireBinding.toolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        bindViewModel()
    }

    override fun onDestroyView() {
        listAdapter = null
        super.onDestroyView()
    }

    private fun setup() {
        setHasOptionsMenu(true)

        with(requireBinding) {
            configToolbar(getString(R.string.bookmarks), R.menu.favorite_toolbar_menu)
            val searchView = toolbar.menu.findItem(R.id.search_bar).actionView as SearchView
            searchView.setOnQueryTextListener(this@FavoritesFragment)

            feedRecycler.apply {
                setHasFixedSize(true)
                adapter = MangaListAdapter(this@FavoritesFragment).also {
                    listAdapter = it
                }
            }

            val spanSizeLookup = DefaultSpanSizeLookup(feedRecycler)
            spanSizeLookup.addViewType(MangaItem.MANGA_GRID_ITEM)
            spanSizeLookup.attachToParent()

            swipeRefreshLayout.isEnabled = false
        }
    }

    private fun bindViewModel() {
        viewModel.content.observe(viewLifecycleOwner) {
            listAdapter?.submitList(it)
        }
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    override fun onClick(view: View, item: MangaItem) {
        val detailsFragment = MangaDetailsFragment.newInstance(item.manga)
        hostActivity?.changeFragment(detailsFragment, true)
    }

    override fun onLongClick(view: View, item: MangaItem): Boolean {
        return false
    }

    override fun onReload() = Unit

    override fun onRetryClick() = Unit

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterFavorite(newText)
        return true
    }
}