package com.vianh.blogtruyen.features.details

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vianh.blogtruyen.features.details.info.InfoPageFragment
import com.vianh.blogtruyen.features.details.comments.CommentPageFragment

// Use view lifecycle to fix leak
class ContentPagerAdapter(fragment: MangaDetailsFragment) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InfoPageFragment.newInstance()
            1 -> CommentPageFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }

}