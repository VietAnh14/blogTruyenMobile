package com.vianh.blogtruyen.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    var binding: B? = null
    val requireBinding: B
        get() = checkNotNull(binding)

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
}