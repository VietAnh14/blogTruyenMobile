package com.vianh.blogtruyen.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.mangaDetails.MangaDetailsFragment
import com.vianh.blogtruyen.utils.GridItemSpacingDecorator
import com.vianh.blogtruyen.utils.ScrollLoadMore
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: BaseFragment<HomeFragmentBinding>(), MangaItemVH.MangaClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)

    val viewModel by viewModel<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    fun observe() {
        viewModel.listContent.observe(viewLifecycleOwner, { onContentChange(it) })
        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
        viewModel.pageReload.observe(viewLifecycleOwner, { onPageReload(it) })
    }

    private fun onPageReload(reload: Boolean?) {
        requireBinding.swipeRefreshLayout.isRefreshing = reload ?: false
    }

    fun onContentChange(mangaItems: List<ListItem>) {
        val adapter = requireBinding.feedRecycler.adapter as MangaFeedAdapter
        adapter.submitList(mangaItems)
    }

    fun setup() {
        val homeActivity = activity as? HomeActivity ?: return
        homeActivity.setupToolbar(requireBinding.toolbar)
        homeActivity.showBottomNav()

        with(requireBinding.feedRecycler) {
            setHasFixedSize(true)
            addItemDecoration(GridItemSpacingDecorator(30))
            adapter = MangaFeedAdapter(this@HomeFragment)
            addOnScrollListener(ScrollLoadMore(2) {
                viewModel.loadPage(append = true)
            })
        }

        requireBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(1, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        val activity = activity ?: return
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        if (searchManager != null) {
            val searchView = ((menu.findItem(R.id.search_bar)).actionView as SearchView)
            searchView.queryHint = "Search manga"
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onMangaItemClick(mangaItem: MangaItem) {
        val activity = activity as HomeActivity
        activity.changeFragment(MangaDetailsFragment.newInstance(mangaItem.manga), true)
    }
}