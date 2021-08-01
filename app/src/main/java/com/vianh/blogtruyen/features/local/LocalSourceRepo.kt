package com.vianh.blogtruyen.features.local

import android.content.Context
import com.vianh.blogtruyen.data.local.MangaDb
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
            .map { it.copy(imageUrl = findCoverById(it.id) ?: "") }
    }

    private fun findCoverById(mangaId: Int): String? {
        return getCoverDir().listFiles()
            ?.first { it.isFile && it.nameWithoutExtension == mangaId.toString() }
            ?.absolutePath
    }

    fun loadMangaDetail(): Manga {
        val coverDir = getCoverDir()
        val localDir = getStorageDir()
        return Manga.getEmpty()
    }

    fun getChapterDir(mangaId: Int, title: String, chapter: Chapter): File {
        val mangaDir = getLocalMangaDir(mangaId, title)
        val chapterDir = File(mangaDir, String.format(FILE_FORMAT, chapter.number, chapter.name))
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