package com.vianh.blogtruyen.ui.base

import android.os.Bundle
import android.view.MenuItem
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

    fun getBinding() = mBinding

    fun getViewModel() = mViewModel

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}