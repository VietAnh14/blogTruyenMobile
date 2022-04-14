package com.vianh.blogtruyen.features.list.filter

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.FilterDialogFragmentBinding
import com.vianh.blogtruyen.features.list.MangaFragment
import com.vianh.blogtruyen.features.list.MangaViewModel
import com.vianh.blogtruyen.utils.screenHeight
import com.vianh.blogtruyen.utils.toPx

class FilterDialogFragment: BottomSheetDialogFragment(), OnApplyWindowInsetsListener {
    lateinit var binding: FilterDialogFragmentBinding
    private val viewModel by lazy { parentViewModel() }
    private var filterAdapter: FilterAdapter? = null
    private var lastInsets: Insets? = null

    private fun parentViewModel(): MangaViewModel {
        val parent = requireParentFragment() as MangaFragment<*>
        return parent.viewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val behavior = (dialog as BottomSheetDialog).behavior
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterDialogFragmentBinding.inflate(inflater, container, false)
        binding.root.minHeight = screenHeight
        ViewCompat.setOnApplyWindowInsetsListener(binding.filterRecycler, this)
        return binding.root
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {
        if (insets == null) return WindowInsetsCompat.CONSUMED
        val barInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (lastInsets == barInsets) return WindowInsetsCompat.CONSUMED

        lastInsets = barInsets
        v?.updatePadding(
            left = barInsets.left,
            right = barInsets.right,
            bottom = barInsets.bottom + 20.toPx
        )
        return WindowInsetsCompat.CONSUMED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        bindViewModel()
    }

    override fun onDestroyView() {
        lastInsets = null
        filterAdapter = null
        super.onDestroyView()
    }

    override fun getTheme(): Int = R.style.Theme_BottomSheetTheme

    fun bindViewModel() {
        viewModel.categoryItems.observe(viewLifecycleOwner, this::onCategoryChange)
    }

    private fun setup() {
        filterAdapter = FilterAdapter()
        binding.clearFilter.setOnClickListener {
            filterAdapter?.clearFilters()
        }

        with(binding.filterRecycler) {
            adapter = filterAdapter
        }
    }

    fun onCategoryChange(items: List<FilterCategoryItem>) {
        filterAdapter?.submitList(items)
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.applyFilter()
        super.onDismiss(dialog)
    }

    companion object {
        fun show(fm: FragmentManager): FilterDialogFragment {
            val dialog = FilterDialogFragment()
            return dialog.also { it.show(fm, null) }
        }

        fun newInstance(): FilterDialogFragment {
            return FilterDialogFragment()
        }
    }
}