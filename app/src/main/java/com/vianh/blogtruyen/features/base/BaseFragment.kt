package com.vianh.blogtruyen.features.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.utils.attrDimenPixel
import com.vianh.blogtruyen.utils.getSurfaceColorPrimary

abstract class BaseFragment<B : ViewBinding> : Fragment(), OnApplyWindowInsetsListener,
    Toolbar.OnMenuItemClickListener {
    var binding: B? = null
    val requireBinding: B
        get() = checkNotNull(binding)

    protected var hostActivity: MainActivity? = null

    protected open fun getToolbar(): Toolbar? = null

    abstract fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): B

    override fun onCreateView(
        inflater: LayoutInflater,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = createBinding(inflater, container, savedInstanceState)
        binding = viewBinding
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view, this)
        setupToolbar()
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        return if (applyInsets(systemBarInsets)) WindowInsetsCompat.CONSUMED else insets
    }

    protected val toolbarHeight by lazy { requireContext().attrDimenPixel(R.attr.actionBarSize) }
    open fun applyInsets(insets: Insets): Boolean {
        val currentToolbar = getToolbar()
        if (currentToolbar != null) {
            val totalToolbarHeight = toolbarHeight + insets.top
            currentToolbar.updateLayoutParams<ViewGroup.LayoutParams> { height = totalToolbarHeight }
            currentToolbar.updatePadding(top = insets.top)
            view?.updatePadding(left = insets.left, right = insets.right)
        } else {
            view?.updatePadding(top = insets.top, left = insets.left, right = insets.right)
        }

        return true
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    fun configToolbar(title: String? = null, @MenuRes menuId: Int? = null) {
        val toolbar = getToolbar() ?: return
        if (menuId != null) {
            toolbar.menu.clear()
            toolbar.inflateMenu(menuId)
        }

        if (title != null) {
            toolbar.title = title
        }
    }

    private fun setupToolbar() {
        val toolbar = getToolbar() ?: return
        if (hostActivity?.canNavigateUp() == true) {
            toolbar.navigationIcon = ContextCompat.getDrawable(
                requireContext(),
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
            )
        }

        toolbar.setBackgroundColor(requireContext().getSurfaceColorPrimary())
        toolbar.setOnMenuItemClickListener(this)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as? MainActivity
    }

    override fun onDetach() {
        hostActivity = null
        super.onDetach()
    }
}