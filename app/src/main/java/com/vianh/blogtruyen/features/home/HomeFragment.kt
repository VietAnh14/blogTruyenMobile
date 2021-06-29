package com.vianh.blogtruyen.features.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.home.list.MangaFeedAdapter
import com.vianh.blogtruyen.features.home.list.MangaItem
import com.vianh.blogtruyen.features.home.list.MangaItemVH
import com.vianh.blogtruyen.features.list.ListItem
import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsFragment
import com.vianh.blogtruyen.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: BaseFragment<HomeFragmentBinding>(), MangaItemVH.MangaClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)

    private val viewModel by viewModel<HomeViewModel>()

    private var feedAdapter: MangaFeedAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.listContent.observeAsLiveData(viewLifecycleOwner, this::onContentChange)
        viewModel.pageReload.observe(viewLifecycleOwner, this::onPageReload)
        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onPageReload(reload: Boolean?) {
        requireBinding.swipeRefreshLayout.isRefreshing = reload ?: false
    }

    private fun onContentChange(mangaItems: List<MangaItem>) {
        feedAdapter?.submitList(mangaItems)
    }

    private fun setup() {
        val homeActivity = activity as? HomeActivity ?: return
        homeActivity.setupToolbar(requireBinding.toolbar)
        homeActivity.showBottomNav()

        with(requireBinding.feedRecycler) {
            setHasFixedSize(true)
            addItemDecoration(GridItemSpacingDecorator(30))
            adapter = MangaFeedAdapter(this@HomeFragment).also { feedAdapter = it }
            addOnScrollListener(ScrollLoadMore(2) {
                viewModel.loadPage()
            })
        }

        requireBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(1, true)
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

    override fun onMangaItemClick(mangaItem: MangaItem) {
        val activity = activity as HomeActivity
        activity.changeFragment(MangaDetailsFragment.newInstance(mangaItem.manga), true)
    }

    override fun onDestroyView() {
        feedAdapter = null
        super.onDestroyView()
    }
}