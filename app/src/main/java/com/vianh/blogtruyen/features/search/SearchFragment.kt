package com.vianh.blogtruyen.features.search

import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.feed.list.NewFeedAdapter
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.views.recycler.ScrollLoadMore
import com.vianh.blogtruyen.utils.hideSoftKeyboard
import com.vianh.blogtruyen.utils.showSoftKeyBoard
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: BaseFragment<HomeFragmentBinding>(), SearchView.OnQueryTextListener, NewFeedAdapter.Callbacks {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(layoutInflater, container, false)
    }

    private val viewModel by viewModel<SearchViewModel>()
    private var searchAdapter: NewFeedAdapter? = null

    override fun getToolbar(): Toolbar? {
        return requireBinding.toolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        bindViewModel()
    }

    private fun setupViews() {
        configToolbar(getString(R.string.search), R.menu.search_menu)

        with(requireBinding.toolbar) {
            title = getString(R.string.search)

            val searchManager = getSystemService(requireContext(), SearchManager::class.java)
            val searchItem = menu.findItem(R.id.search_bar)
            val searchView = searchItem.actionView as SearchView

            searchView.setSearchableInfo(searchManager?.getSearchableInfo(hostActivity?.componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnQueryTextListener(this@SearchFragment)

            if (!viewModel.searchInit) {
                searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        v.findFocus().showSoftKeyBoard()
                    }
                }

                searchItem.expandActionView()
                searchView.requestFocus()
                viewModel.searchInit = true
            }
        }

        with(requireBinding.feedRecycler) {
            searchAdapter = NewFeedAdapter(this@SearchFragment)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
            addOnScrollListener(ScrollLoadMore(0) {
                viewModel.loadMore(findSearchView().query?.toString())
            })
        }

        requireBinding.swipeRefreshLayout.isEnabled = false
    }

    private fun findSearchView(): SearchView {
        return requireBinding.toolbar.menu.findItem(R.id.search_bar).actionView as SearchView
    }

    private fun bindViewModel() {
        viewModel.contents.observe(viewLifecycleOwner) { searchAdapter?.submitList(it) }
        viewModel.error.observe(viewLifecycleOwner) { showToast(it?.message) }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        requireBinding.feedRecycler.scrollTo(0, 0)
        val focus = activity?.currentFocus
        focus?.hideSoftKeyboard()
        focus?.clearFocus()
        viewModel.search(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onRetryClick() {
        viewModel.loadMore(findSearchView().query?.toString())
    }

    override fun onReload() {
        viewModel.search(findSearchView().query?.toString())
    }

    override fun onClick(view: View, item: MangaItem) {
        hostActivity?.changeFragment(MangaDetailsFragment.newInstance(item.manga, false), true)
    }

    override fun onLongClick(view: View, item: MangaItem): Boolean {
        return false
    }

    override fun onDestroyView() {
        searchAdapter = null
        super.onDestroyView()
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_key"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}