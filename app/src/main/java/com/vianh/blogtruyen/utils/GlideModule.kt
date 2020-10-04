package com.vianh.blogtruyen.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import java.io.InputStream

@GlideModule
class GlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).signature(
                ObjectKey(System.currentTimeMillis().toShort())
            )

            this.setDiskCache(InternalCacheDiskCacheFactory(context, 400 * 1024 * 1024))
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = BlogtruyenProvider.client
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client))
    }
}