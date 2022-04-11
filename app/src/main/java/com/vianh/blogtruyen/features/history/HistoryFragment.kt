package com.vianh.blogtruyen.features.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HistoryFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment: BaseFragment<HistoryFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HistoryFragmentBinding {
        return HistoryFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel: HistoryViewModel by viewModel()

    private var historyAdapter: HistoryAdapter? = null

    override fun getToolbar(): Toolbar = requireBinding.toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.content.observe(viewLifecycleOwner) {
            historyAdapter?.submitList(it)
        }

        viewModel.toInfoCommand.observe(viewLifecycleOwner) {
            hostActivity?.changeFragment(MangaDetailsFragment.newInstance(it), true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_toolbar_menu, menu)
        val searchView = menu.findItem(R.id.search_bar).actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterHistory(newText ?: return false)
                return false
            }

        })
    }

    private fun setup() {
        setHasOptionsMenu(true)

        with(requireBinding.contentRecycler) {
            setHasFixedSize(true)
            adapter = HistoryAdapter(viewModel).also { historyAdapter = it }
        }

        with(requireBinding.toolbar) {
            val searchView = menu.findItem(R.id.search_bar).actionView as SearchView

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.filterHistory(newText ?: return false)
                    return false
                }
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_all -> {
                viewModel.clearAllHistory()
                true
            }

            else -> super.onMenuItemClick(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historyAdapter = null
    }

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}