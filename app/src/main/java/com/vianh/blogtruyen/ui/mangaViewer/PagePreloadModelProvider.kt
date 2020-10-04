package com.vianh.blogtruyen.ui.mangaViewer

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.Target
import java.util.*

class PagePreloadModelProvider(private val listUrl: List<String>, private val context: Context) :
    ListPreloader.PreloadModelProvider<String> {
    override fun getPreloadItems(position: Int): MutableList<String> {
        return Collections.singletonList(listUrl[position])
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return Glide
            .with(context)
            .asFile()
            .load(item)
            .override(Target.SIZE_ORIGINAL)
    }

}