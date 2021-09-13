package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder

class LoadingItemVH(val binding: LoadingPageItemBinding): AbstractViewHolder<ReaderItem.LoadingItem, Unit>(binding.root) {
    override fun onBind(data: ReaderItem.LoadingItem, extra: Unit) {
        binding.progressCircular.show()
    }

}