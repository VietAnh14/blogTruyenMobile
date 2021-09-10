package com.vianh.blogtruyen.features.feed

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.FeedFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.list.HomeFragment
import com.vianh.blogtruyen.features.search.SearchFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class NewFeedFragment : BaseFragment<FeedFragmentBinding>(), SwipeRefreshLayout.OnRefreshListener,
    NewFeedAdapter.ItemClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FeedFragmentBinding {
        return FeedFragmentBinding.inflate(inflater, container, false)
    }

    // Scope to activity
    private val viewModel by sharedViewModel<NewFeedViewModel>()

    private var pinAdapter: NewFeedAdapter? = null
    private var updateAdapter: NewFeedAdapter? = null
    private var newStoriesAdapter: NewFeedAdapter? = null
    private var historyAdapter: NewFeedAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        bindViewModel()
    }

    private fun setupView() {
        // TODO: CHANGE TO USE RECYCLER VIEW INSTEAD OF SCROLL VIEW
        with(requireBinding) {
            swipeRefreshLayout.setOnRefreshListener(this@NewFeedFragment)

            pinStoriesRecycler.layoutManager = object: LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = (width * 0.85).toInt()
                    return super.checkLayoutParams(lp)
                }
            }

            pinStoriesRecycler.adapter =
                NewFeedAdapter(this@NewFeedFragment).also { pinAdapter = it }
            PagerSnapHelper().attachToRecyclerView(pinStoriesRecycler)
        }

        with(requireBinding.updateManga) {
            title.setText(R.string.recent_update)
            seeAll.setOnClickListener { onSeeAllClick(NewFeedItem.NEW_STORIES_ITEM) }
            contentRecycler.apply {
                layoutManager = createLayoutManager()
                adapter = NewFeedAdapter(this@NewFeedFragment).also { updateAdapter = it }
            }
        }

        with(requireBinding.newStories) {
            title.setText(R.string.new_stories)
            seeAll.setOnClickListener { onSeeAllClick(NewFeedItem.UPDATE_ITEM) }
            contentRecycler.apply {
                layoutManager = createLayoutManager()
                adapter = NewFeedAdapter(this@NewFeedFragment).also { newStoriesAdapter = it }
            }
        }

        with(requireBinding.continueReading) {
            title.setText(R.string.continue_reading)
            seeAll.setOnClickListener { onSeeAllClick(NewFeedItem.HISTORY_ITEM) }
            contentRecycler.apply {
                layoutManager = createLayoutManager()
                adapter = NewFeedAdapter(this@NewFeedFragment).also { historyAdapter = it }
            }
        }

        inflateToolBar()
    }

    private fun inflateToolBar() {
        with(requireBinding.toolbar) {
            setupToolbar(this)

            val activity = activity ?: return
            val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as? SearchManager

            if (searchManager != null) {
                val searchItem = menu.findItem(R.id.search_bar)
                val searchView = searchItem.actionView as SearchView

                searchView.maxWidth = Int.MAX_VALUE
                searchView.requestFocus()
                searchView.queryHint = getString(R.string.search)
                searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query.isNullOrBlank()) {
                            return false
                        }

                        searchView.clearFocus()
                        hostActivity?.changeFragment(SearchFragment.newInstance(query), true)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_bar -> {
                true
            }
            R.id.refresh -> {
                viewModel.loadFeed()
                true
            }
            else -> super.onMenuItemClick(item)
        }
    }

    private fun onSeeAllClick(viewType: Int) {
        when (viewType) {
            NewFeedItem.NEW_STORIES_ITEM -> {
                hostActivity?.changeFragment(HomeFragment.newInstance(), true)
            }

            NewFeedItem.UPDATE_ITEM -> {
                hostActivity?.changeFragment(HomeFragment.newInstance(), true)
            }

            NewFeedItem.HISTORY_ITEM -> {
                hostActivity?.selectBottomNavItem(R.id.history)
            }

            else -> throw IllegalArgumentException("Unknown navigation type $viewType")
        }
    }

    // FIXME
    private fun startRandomScroll() {
        with(requireBinding.pinStoriesRecycler) {
            handler.postDelayed({
                val pos = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val itemCount = adapter?.itemCount ?: 0
                val nexPos = if (pos >= itemCount - 1) 0 else pos + 1
                smoothScrollToPosition(nexPos)
                startRandomScroll()
            }, TimeUnit.SECONDS.toMillis(10))
        }
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun bindViewModel() {
        viewModel.pinItems.observe(viewLifecycleOwner, this::onContentChange)
        viewModel.isLoading.observe(viewLifecycleOwner, this::onLoadingChange)
        viewModel.historyItem.observe(viewLifecycleOwner, this::onHistoryChange)
        viewModel.updateItems.observe(viewLifecycleOwner, this::onNewUploadChange)
        viewModel.newStoriesItems.observe(viewLifecycleOwner, this::onNewStoriesChange)

        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onContentChange(items: List<NewFeedItem>) {
        pinAdapter?.submitList(items)

    }

    private fun onHistoryChange(items: List<NewFeedItem>) {
        historyAdapter?.submitList(items)
    }

    private fun onNewUploadChange(items: List<NewFeedItem>) {
        updateAdapter?.submitList(items)
    }

    private fun onNewStoriesChange(items: List<NewFeedItem>) {
        newStoriesAdapter?.submitList(items)
    }

    private fun onLoadingChange(isLoading: Boolean) {
        requireBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    override fun onRefresh() {
        viewModel.loadFeed()
    }


    companion object {
        fun newInstance(): NewFeedFragment {
            return NewFeedFragment()
        }
    }

    override fun onItemClick(item: NewFeedItem.MangaItem) {
        hostActivity?.changeFragment(MangaDetailsFragment.newInstance(item.item), true)
    }

    override fun onDestroyView() {
        newStoriesAdapter = null
        updateAdapter = null
        pinAdapter = null
        historyAdapter = null
        super.onDestroyView()
    }
}