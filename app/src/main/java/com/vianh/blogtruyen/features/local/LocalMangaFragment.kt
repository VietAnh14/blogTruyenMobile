package com.vianh.blogtruyen.features.local

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.features.list.MangaListAdapter
import com.vianh.blogtruyen.utils.DefaultSpanSizeLookup
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalMangaFragment : BaseFragment<HomeFragmentBinding>(),
    SwipeRefreshLayout.OnRefreshListener, MangaListAdapter.Callback {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModel<LocalViewModel>()
    private var adapter: MangaListAdapter? = null

    override fun getToolbar(): Toolbar = requireBinding.toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        bindViewModel()
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }

    private fun setUpView() {
        with(requireBinding) {
            configToolbar(resources.getString(R.string.downloaded))
            swipeRefreshLayout.setOnRefreshListener(this@LocalMangaFragment)

            val spanSizeLookup = DefaultSpanSizeLookup(feedRecycler)
            spanSizeLookup.addViewType(MangaItem.MANGA_GRID_ITEM)
            spanSizeLookup.attachToParent()

            adapter = MangaListAdapter(this@LocalMangaFragment)
            feedRecycler.adapter = adapter
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            requireBinding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.mangaContent.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.deleteCompleteEvent.observe(viewLifecycleOwner) {
            showToast(getString(R.string.deleted_message, it))
            viewModel.loadMangaList()
        }

        viewModel.error.observe(viewLifecycleOwner) { showToast(it.message) }
    }

    override fun onClick(view: View, item: MangaItem) {
        hostActivity?.changeFragment(MangaDetailsFragment.newInstance(item.manga, true), true)
    }

    override fun onLongClick(view: View, item: MangaItem): Boolean {
        val popUp = PopupMenu(requireActivity(), view)
        popUp.inflate(R.menu.manga_popup_menu)
        popUp.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    viewModel.deleteLocalManga(item.manga)
                    true
                }

                else -> false
            }
        }
        popUp.show()
        return true
    }

    override fun onReload() = Unit

    override fun onRetryClick() = Unit

    override fun onRefresh() {
        viewModel.loadMangaList()
    }

    companion object {
        fun newInstance(): LocalMangaFragment {
            return LocalMangaFragment()
        }
    }
}