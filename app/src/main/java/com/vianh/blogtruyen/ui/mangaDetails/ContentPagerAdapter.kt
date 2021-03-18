package com.vianh.blogtruyen.ui.mangaDetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vianh.blogtruyen.ui.mangaDetails.chapter.ChapterPageFragment
import com.vianh.blogtruyen.ui.mangaDetails.comments.CommentPageFragment

class ContentPagerAdapter(fragment: MangaDetailsFragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChapterPageFragment.newInstance()
            1 -> CommentPageFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }

}