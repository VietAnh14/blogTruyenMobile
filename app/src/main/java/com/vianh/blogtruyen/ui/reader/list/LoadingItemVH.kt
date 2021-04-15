package com.vianh.blogtruyen.ui.reader.list

import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.ui.list.BaseVH
import com.vianh.blogtruyen.ui.list.ListItem

class LoadingItemVH(binding: LoadingPageItemBinding): BaseVH<LoadingPageItemBinding>(binding) {

    override fun onBind(item: ListItem) {
        binding.progressCircular.show()
    }
}