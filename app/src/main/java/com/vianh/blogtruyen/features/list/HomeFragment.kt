package com.vianh.blogtruyen.features.list

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: BaseFragment<HomeFragmentBinding>(), MangaItemVH.MangaClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)

    private val viewModel by viewModel<HomeViewModel>()

    private var listAdapter: MangaListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.content.observe(viewLifecycleOwner, this::onContentChange)
        viewModel.pageReload.observe(viewLifecycleOwner, this::onPageReload)
        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onPageReload(reload: Boolean?) {
        requireBinding.swipeRefreshLayout.isRefreshing = reload ?: false
    }

    private fun onContentChange(mangaItems: List<MangaItem>) {
        listAdapter?.submitList(mangaItems)
    }

    private fun setup() {
        setupToolbar(requireBinding.toolbar)

        with(requireBinding.feedRecycler) {
            setHasFixedSize(true)
//            addItemDecoration(GridItemSpacingDecorator(20))
            adapter = MangaListAdapter(this@HomeFragment).also { listAdapter = it }
            addOnScrollListener(ScrollLoadMore(2) {
                viewModel.loadPage()
            })
        }

        requireBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(1)
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

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            lifecycleScope.launch {
                requireBinding.feedRecycler.scrollToPosition(0)
                viewModel.loadPage(1)
            }
            return true
        }

        return super.onMenuItemClick(item)
    }

    override fun onMangaItemClick(mangaItem: MangaItem) {
        val activity = activity as MainActivity
        activity.changeFragment(MangaDetailsFragment.newInstance(mangaItem.manga), true)
    }

    override fun onDestroyView() {
        listAdapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}