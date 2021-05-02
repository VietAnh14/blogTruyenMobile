package com.vianh.blogtruyen

import com.vianh.blogtruyen.databinding.MangaInfoItemBinding
import com.vianh.blogtruyen.ui.base.BaseActivityVB

class TestActivity: BaseActivityVB<MangaInfoItemBinding>() {
    override fun createBinding(): MangaInfoItemBinding {
        return MangaInfoItemBinding.inflate(layoutInflater)
    }
}