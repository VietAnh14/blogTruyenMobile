package com.vianh.blogtruyen.features.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.home.list.MangaFeedAdapter
import com.vianh.blogtruyen.features.home.list.MangaItem
import com.vianh.blogtruyen.features.home.list.MangaItemVH
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalMangaFragment : BaseFragment<HomeFragmentBinding>(),
    SwipeRefreshLayout.OnRefreshListener, MangaItemVH.MangaClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModel<LocalViewModel>()
    private var adapter: MangaFeedAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        bindViewModel()
    }

    private fun setUpView() {
        with(requireBinding) {
            hostActivity?.setupToolbar(toolbar, resources.getString(R.string.downloaded))

            adapter = MangaFeedAdapter(this@LocalMangaFragment)
            feedRecycler.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener(this@LocalMangaFragment)
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            requireBinding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.mangaContent.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.error.observe(viewLifecycleOwner) { showToast(it.message) }
    }

    override fun onMangaItemClick(mangaItem: MangaItem) {
        hostActivity?.changeFragment(MangaDetailsFragment.newInstance(mangaItem.manga, true), true)
    }

    override fun onRefresh() {
        viewModel.loadMangaList()
    }
}