package com.vianh.blogtruyen.features.list

import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.features.base.BaseViewHolder

// TODO: ADD BASE ADAPTER
abstract class BaseVH<out B: ViewBinding>(val binding: B) :
    BaseViewHolder(binding.root)