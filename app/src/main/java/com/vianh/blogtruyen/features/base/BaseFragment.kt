package com.vianh.blogtruyen.features.base

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.main.MainActivity

abstract class BaseFragment<B : ViewBinding> : Fragment(), OnApplyWindowInsetsListener,
    Toolbar.OnMenuItemClickListener {
    var binding: B? = null
    val requireBinding: B
        get() = checkNotNull(binding)

    var hostActivity: MainActivity? = null

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
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view?.updatePadding(top = systemBarInsets.top, left = systemBarInsets.left, right = systemBarInsets.right)
        return WindowInsetsCompat.CONSUMED
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    fun setupToolbar(toolbar: Toolbar, title: String? = null, @MenuRes menuId: Int? = null) {
        if (menuId != null) {
            toolbar.menu.clear()
            toolbar.inflateMenu(menuId)
        }

        if (title != null)
            toolbar.title = title

        if (hostActivity?.canNavigateUp() == true) {
            toolbar.navigationIcon = ContextCompat.getDrawable(
                requireContext(),
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
            )
        }

        toolbar.popupTheme = R.style.PopupMenu
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