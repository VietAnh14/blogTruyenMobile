package com.vianh.blogtruyen.ui.list

import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.ui.base.BaseViewHolder

abstract class BaseVH<out B: ViewBinding>(val binding: B) :
    BaseViewHolder(binding.root)