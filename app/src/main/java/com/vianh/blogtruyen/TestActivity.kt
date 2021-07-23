package com.vianh.blogtruyen

import android.os.Bundle
import com.vianh.blogtruyen.databinding.MangaInfoItemBinding
import com.vianh.blogtruyen.features.base.BaseActivity

class TestActivity: BaseActivity<MangaInfoItemBinding>() {
    override fun createBinding(): MangaInfoItemBinding {
        return MangaInfoItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val result = runCatching {
            "view model"
        }
    }
}