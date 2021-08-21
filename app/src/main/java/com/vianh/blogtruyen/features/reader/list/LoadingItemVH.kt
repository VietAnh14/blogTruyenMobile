package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.BaseVH
import com.vianh.blogtruyen.features.base.list.ListItem

class LoadingItemVH(val binding: LoadingPageItemBinding): AbstractViewHolder<ReaderItem.LoadingItem, Unit>(binding.root) {
    override fun onBind(data: ReaderItem.LoadingItem, extra: Unit) {
        binding.progressCircular.show()
    }

}