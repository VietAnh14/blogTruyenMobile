package com.vianh.blogtruyen.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vianh.blogtruyen.ViewModelFactory

abstract class BaseActivity<V: ViewModel, B: ViewDataBinding>: AppCompatActivity() {
    private val mViewModel: V by lazy {
        createViewModel()
    }

    private lateinit var mBinding: B

    public fun getBinding() = mBinding

    public fun getViewModel() = mViewModel

    abstract fun getViewModelClass(): Class<V>

    abstract fun getBindingVariable(): Int

    @LayoutRes
    abstract fun getLayoutId(): Int

    fun createViewModel(): V {
        return ViewModelProvider(this, ViewModelFactory()).get(getViewModelClass())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.setVariable(getBindingVariable(), mViewModel)
        mBinding.lifecycleOwner = this
    }
}