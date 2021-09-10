package com.vianh.blogtruyen.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment: BaseFragment<HomeFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(layoutInflater, container, false)
    }

    private var toolBarInit = false

    private val viewModel by viewModel<SearchViewModel> {
        parametersOf(arguments?.getString(SEARCH_QUERY_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        setupToolbar(requireBinding.toolbar, getString(R.string.search))

        if (toolBarInit)
            return

        with(requireBinding.toolbar) {
            val searchItem = menu.findItem(R.id.search_bar)
            val searchView = searchItem.actionView as SearchView
            searchView.maxWidth = Int.MAX_VALUE
            searchItem.expandActionView()
            searchView.setQuery(viewModel.searchQuery.value, false)
            searchView.clearFocus()
            title = getString(R.string.search)
        }
        toolBarInit = true
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_key"

        fun newInstance(searchQuery: String): SearchFragment {
            val args = Bundle(1).apply {
                putString(SEARCH_QUERY_KEY, searchQuery)
            }
            return SearchFragment().apply { arguments = args }
        }
    }
}