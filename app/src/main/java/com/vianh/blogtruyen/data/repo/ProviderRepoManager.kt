package com.vianh.blogtruyen.data.repo

class ProviderRepoManager(private val localProvider: LocalSourceRepo, private val remoteRepo: MangaProviderRepoImpl) {

    fun create(isLocal: Boolean): MangaProviderRepo {
        return if (isLocal) {
            localProvider
        } else {
            remoteRepo
        }
    }
}