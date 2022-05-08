package com.vianh.blogtruyen.data.repo

import android.content.Context
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.FeedItem
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.ext.toSafeFileName
import java.io.File

class LocalSourceRepo(private val context: Context, private val db: MangaDb): MangaProviderRepo {

    override suspend fun getList(page: Int): List<Manga> {
        val mangaDir = getStorageDir()

        val listMangaDirs = mangaDir.listFiles { dir, name ->
            dir.isDirectory && name.split("_").lastOrNull()?.toIntOrNull() != null
        } ?: return emptyList()

        listMangaDirs.sortByDescending { it.lastModified() }

        return listMangaDirs
            .map { it.name.split("_").last().toInt() }
            .mapNotNull { db.mangaDao.getMangaById(it) }
            .map { it.toManga() }
            .map { it.copy(imageUrl = findCoverById(it.id)?.absolutePath.orEmpty()) }
    }

    override suspend fun getMangaDetails(manga: Manga): Manga {
        return db.mangaDao.getMangaById(manga.id)?.toManga() ?: throw IllegalStateException("Manga not found")
    }

    override suspend fun getChapters(mangaId: Int): List<Chapter> {
        val localDir = getStorageDir()
        val mangaDir = localDir.listFiles { dir, name ->
            dir.isDirectory && name.split("_").lastOrNull()?.toIntOrNull() == mangaId
        }?.firstOrNull() ?: return emptyList()

        val chapterDirs = mangaDir.listFiles() ?: return emptyList()

        return chapterDirs.mapNotNull {
            val id = it.name.split("_")
                .getOrNull(1) ?: return@mapNotNull null

            val chapter = getChapterById(id)
            if (chapter == null) {
                it.delete()
            }

            chapter
        }.sortedByDescending { it.number }
    }

    override suspend fun getChapterPages(chapter: Chapter, mangaId: Int): List<String> {
        val manga = db.mangaDao.requireManga(mangaId)
        val chapterDir = getChapterDir(manga.mangaId, manga.title, chapter)
        if (!chapterDir.exists()) {
            throw IllegalStateException("Could not find chapter in local source")
        }

        val pages = chapterDir.listFiles() ?: throw IllegalStateException("No page found")
        return pages
            .filter { it.nameWithoutExtension.toIntOrNull() != null }
            .sortedBy { it.nameWithoutExtension }
            .map { it.absolutePath }
    }

    override suspend fun getComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>> = emptyMap()

    override suspend fun getNewFeed(): FeedItem {
        throw UnsupportedOperationException("Local manga not support new feed")
    }

    fun findCoverById(mangaId: Int): File? {
        return getCoverDir().listFiles()
            ?.firstOrNull { it.isFile && it.nameWithoutExtension == mangaId.toString() }
    }

    suspend fun getChapterById(id: String): Chapter? {
        return db.chapterDao.findChapterById(id)?.toChapter()
    }

    fun getChapterDir(mangaId: Int, title: String, chapter: Chapter): File {
        val mangaDir = getLocalMangaDir(mangaId, title)
        return File(mangaDir, CHAPTER_FORMAT.format(chapter.number, chapter.id))
    }

    fun getLocalMangaDir(id: Int, title: String): File {
        val fileDir = getStorageDir()
        return File(fileDir, MANGA_FORMAT.format(title.toSafeFileName(), id))
    }

    fun getStorageDir(): File {
        return File(context.getExternalFilesDir(null), LOCAL_MANGA_DIR)
    }

    fun getCoverDir(): File {
        return File(context.getExternalFilesDir(null), COVER_DIR).apply {
            if (!exists())
                mkdir()
        }
    }

    fun deleteLocalManga(manga: Manga) {
        val mangaDir = getLocalMangaDir(manga.id, manga.title)
        mangaDir.deleteRecursively()
        val cover = getCoverDir().listFiles { _, name ->
            name.substringBeforeLast(".") == manga.id.toString()
        }
        cover?.getOrNull(0)?.delete()
    }

    companion object {

        const val REPO_NAME = "local"
        const val COVER_DIR = "covers"
        const val LOCAL_MANGA_DIR = "localManga"
        const val MANGA_FORMAT = "%s_%d"
        const val CHAPTER_FORMAT = "%03d_%s"
    }
}