package com.vianh.blogtruyen.features.local

import android.content.Context
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.entity.ChapterEntity
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.toSafeFileName
import java.io.File

class LocalSourceRepo(private val context: Context, private val db: MangaDb) {


    suspend fun getMangaList(): List<Manga> {
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

    fun findCoverById(mangaId: Int): File? {
        return getCoverDir().listFiles()
            ?.firstOrNull { it.isFile && it.nameWithoutExtension == mangaId.toString() }
    }

    suspend fun getChapters(mangaId: Int): List<Chapter> {
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

    suspend fun getChapterById(id: String): Chapter? {
        return db.chapterDao.findChapterById(id)?.toChapter()
    }

    fun loadPages(manga: Manga, chapter: Chapter): List<String> {
        val chapterDir = getChapterDir(manga.id, manga.title, chapter)
        if (!chapterDir.exists())
            throw IllegalStateException("Could not find chapter in local source")

        val pages = chapterDir.listFiles() ?: throw IllegalStateException("No page found")
        pages.sortBy { it.nameWithoutExtension }
        return pages
            .filter { it.nameWithoutExtension.toIntOrNull() != null }
            .map { it.absolutePath }
    }

    suspend fun upsertChapter(chapter: Chapter, mangaId: Int) {
        val readState = db.chapterDao.findChapterById(chapter.id)?.isRead ?: false
        chapter.read = readState
        val entity = ChapterEntity.fromChapter(chapter, mangaId)
        db.chapterDao.upsert(entity)
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

        const val COVER_DIR = "covers"

        const val LOCAL_MANGA_DIR = "localManga"

        const val MANGA_FORMAT = "%s_%d"

        const val CHAPTER_FORMAT = "%03d_%s"
    }
}