package com.vianh.blogtruyen.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.ui.home.HomeActivity

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    var binding: B? = null
    val requireBinding: B
        get() = checkNotNull(binding)

    var hostActivity: HomeActivity? = null

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = createBinding(inflater, container, savedInstanceState)
        binding = viewBinding
        return viewBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            hostActivity?.navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupToolbar(toolbar: Toolbar) {
        (activity as HomeActivity).setupToolbar(toolbar)
    }

    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as? HomeActivity
    }

    override fun onDetach() {
        hostActivity = null
        super.onDetach()
    }
}