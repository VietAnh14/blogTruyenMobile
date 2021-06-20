package com.vianh.blogtruyen.features.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HistoryFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsFragment
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

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterHistory(newText ?: return false)
                return false
            }

        })
    }

    private fun setup() {
        hostActivity?.setupToolbar(requireBinding.toolbar)
        hostActivity?.showBottomNav()
        setHasOptionsMenu(true)

        with(requireBinding.contentRecycler) {
            setHasFixedSize(true)
            adapter = HistoryAdapter(viewModel).also { historyAdapter = it }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.clear_all) {
            viewModel.clearAllHistory()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historyAdapter = null
    }
}