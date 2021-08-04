package com.vianh.blogtruyen.features.local

import android.content.Context
import com.vianh.blogtruyen.data.local.MangaDb
import com.vianh.blogtruyen.data.local.entity.ChapterEntity
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
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
        }
    }

    suspend fun getChapterById(id: String): Chapter? {
        return db.chapterDao.findChapterById(id)?.toChapter()
    }

    suspend fun upsertChapter(chapter: Chapter, mangaId: Int) {
        // TODO: Keep read state
        val entity = ChapterEntity.fromChapter(chapter, mangaId)
        db.chapterDao.upsert(entity)
    }

    fun getChapterDir(mangaId: Int, title: String, chapter: Chapter): File {
        val mangaDir = getLocalMangaDir(mangaId, title)
        val chapterDir = File(mangaDir, String.format(FILE_FORMAT, chapter.number, chapter.id))
        chapterDir.createDirs()

        return chapterDir
    }

    fun getLocalMangaDir(id: Int, title: String): File {
        val fileDir = getStorageDir()
        val mangaDir = File(fileDir, FILE_FORMAT.format(title, id))
        mangaDir.createDirs()
        return mangaDir
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

    fun File.createDirs() {
        if (!exists())
            mkdirs()
    }

    companion object {

        const val COVER_DIR = "covers"

        const val LOCAL_MANGA_DIR = "localManga"

        const val FILE_FORMAT = "%s_%s"
    }
}