package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.features.list.BaseVH
import com.vianh.blogtruyen.features.list.ListItem

class LoadingItemVH(binding: LoadingPageItemBinding): BaseVH<LoadingPageItemBinding>(binding) {

    override fun onBind(item: ListItem) {
        binding.progressCircular.show()
    }
}