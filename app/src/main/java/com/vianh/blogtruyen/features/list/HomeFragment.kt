package com.vianh.blogtruyen.features.list

import android.os.Bundle
import android.view.*
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.list.filter.FilterDialogFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.features.search.SearchFragment
import com.vianh.blogtruyen.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: BaseFragment<HomeFragmentBinding>(), ItemClick<MangaItem> {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)

    private val viewModel by viewModel<HomeViewModel>()
    private var listAdapter: MangaListAdapter? = null
    private val filterDialogFragment by lazy { FilterDialogFragment.newInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        val barInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        requireBinding.btnFilter.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = barInsets.right + 10.0.toPx.toInt()
            bottomMargin = barInsets.bottom + 10.0.toPx.toInt()
        }
        return super.onApplyWindowInsets(v, insets)
    }

    private fun observe() {
        viewModel.content.observe(viewLifecycleOwner, this::onContentChange)
        viewModel.pageReload.observe(viewLifecycleOwner, this::onPageReload)
        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onPageReload(reload: Boolean?) {
        requireBinding.swipeRefreshLayout.isRefreshing = reload ?: false
    }

    private fun onContentChange(mangaItems: List<ListItem>) {
        listAdapter?.submitList(mangaItems)
    }

    private fun setup() {
        setupToolbar(requireBinding.toolbar, getString(R.string.app_name), R.menu.home_toolbar_menu)

        with(requireBinding.feedRecycler) {
            setHasFixedSize(true)
//            addItemDecoration(GridItemSpacingDecorator(20))
            adapter = MangaListAdapter(this@HomeFragment).also { listAdapter = it }
            addOnScrollListener(ScrollLoadMore(2) {
                viewModel.loadNextPage()
            })
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                var lastDy = 0
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val newState = lastDy * dy
                    lastDy = dy
                    if (newState >= 0) {
                        return
                    }

                    if (dy > 0) {
                        requireBinding.btnFilter.extend()
                    } else {
                        requireBinding.btnFilter.shrink()
                    }
                }
            })

            val spanSizeLookup = DefaultSpanSizeLookup(this)
            spanSizeLookup.addViewType(MangaItem.MANGA_GRID_ITEM)
            spanSizeLookup.attachToParent()

        }

        requireBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(1)
        }

        requireBinding.btnFilter.setOnClickListener {
            filterDialogFragment.show(childFragmentManager, null)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                requireBinding.feedRecycler.scrollToPosition(0)
                viewModel.loadPage(1)
                true
            }
            R.id.search_bar -> {
                hostActivity?.changeFragment(SearchFragment.newInstance(), true)
                true
            }

            R.id.list_mode -> {
                showListModeDialog()
                true
            }
            else -> super.onMenuItemClick(item)
        }
    }

    private fun showListModeDialog() {
        val listModes = ListMode.values().map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(requireActivity(), R.style.PopupMenu)
            .setTitle("Select list mode")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setItems(listModes) { dialog, which ->
                dialog.dismiss()
                viewModel.saveListMode(ListMode.valueOf(listModes[which]))
            }.show()
    }

    override fun onClick(view: View, item: MangaItem) {
        val activity = activity as MainActivity
        activity.changeFragment(MangaDetailsFragment.newInstance(item.manga), true)
    }

    override fun onLongClick(view: View, item: MangaItem): Boolean {
        return false
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