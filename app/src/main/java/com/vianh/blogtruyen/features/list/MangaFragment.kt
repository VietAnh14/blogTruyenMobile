package com.vianh.blogtruyen.features.list

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.list.filter.FilterDialogFragment
import com.vianh.blogtruyen.views.recycler.ScrollLoadMore
import com.vianh.blogtruyen.utils.ext.getThemeColor
import com.vianh.blogtruyen.utils.ext.toPx

abstract class MangaFragment<VM : MangaViewModel> : BaseFragment<HomeFragmentBinding>(),
    MangaListAdapter.Callback, SwipeRefreshLayout.OnRefreshListener {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    abstract val viewModel: VM
    protected var mangaAdapter: MangaListAdapter? = null
    private val filterDialogFragment by lazy { FilterDialogFragment.newInstance() }

    abstract val title: String

    override fun getToolbar(): Toolbar? {
        return requireBinding.toolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun setup() {
        configToolbar(title, R.menu.home_toolbar_menu)
        mangaAdapter = MangaListAdapter(this)
        with(requireBinding.feedRecycler) {
            setHasFixedSize(true)
            adapter = mangaAdapter
            addOnScrollListener(ScrollLoadMore(4, viewModel::loadNextPage))
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                var lastDy = 0
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val newState = lastDy * dy
                    lastDy = dy
                    if (newState > 0) {
                        return
                    }

                    if (dy >= 0) {
                        requireBinding.btnFilter.shrink()
                    } else {
                        requireBinding.btnFilter.extend()
                    }
                }
            })
        }

        requireBinding.swipeRefreshLayout.setOnRefreshListener(this)
        requireBinding.btnFilter.show()
        requireBinding.btnFilter.setOnClickListener {
            filterDialogFragment.show(childFragmentManager, null)
        }

        with(requireBinding.toolbar) {
            val searchView = menu.findItem(R.id.search_bar).actionView as? SearchView ?: return@with

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.search(newText)
                    return false
                }
            })
        }
    }

    private fun observe() {
        viewModel.content.observe(viewLifecycleOwner) {
            mangaAdapter?.submitList(it)
        }

        viewModel.categoryItems.observe(viewLifecycleOwner) { items ->
            val hasFilter = items.any { it.isSelected }
            val colorId = if (hasFilter) R.attr.colorSecondary else R.attr.colorOnSurface
            val filterColor = requireContext().getThemeColor(colorId)
            requireBinding.btnFilter.iconTint = ColorStateList.valueOf(filterColor)
        }
    }

    override fun applyInsets(insets: Insets): Boolean {
        super.applyInsets(insets)
        requireBinding.btnFilter.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = insets.right + 10.toPx
            bottomMargin = insets.bottom + 10.toPx
        }

        return true
    }

    override fun onRefresh() {
        viewModel.reload()
    }

    override fun onRetryClick() {
        viewModel.loadNextPage()
    }

    override fun onReload() {
        viewModel.reload()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        super.onMenuItemClick(item)
        when(item.itemId) {
            R.id.list_mode -> showListModeDialog()
            R.id.refresh -> viewModel.reload()
            else -> return false
        }

        return true
    }

    override fun onDestroyView() {
        mangaAdapter = null
        super.onDestroyView()
    }

    private fun showListModeDialog() {
        val listModes = ListMode.values().map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Select list mode")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setItems(listModes) { dialog, which ->
                dialog.dismiss()
                viewModel.saveListMode(ListMode.valueOf(listModes[which]))
            }.show()
    }
}