package com.vianh.blogtruyen.features.details.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.details.ui.info.InfoPageFragment
import com.vianh.blogtruyen.features.details.ui.comments.CommentPageFragment

// Use view lifecycle to fix leak
class ContentPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle), TabLayoutMediator.TabConfigurationStrategy {

    val context = fragment.requireContext()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InfoPageFragment.newInstance()
            1 -> CommentPageFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = when (position) {
            0 -> context.getString(R.string.manga_info)
            1 -> context.getString(R.string.comment)
            else -> "Unknown"
        }
    }

}